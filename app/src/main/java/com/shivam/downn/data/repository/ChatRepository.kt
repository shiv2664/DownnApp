package com.shivam.downn.data.repository

import com.shivam.downn.data.api.ChatApi
import com.shivam.downn.data.local.SessionManager
import com.shivam.downn.data.models.ChatListResponse
import com.shivam.downn.data.models.ChatMessageResponse
import com.shivam.downn.data.network.NetworkResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val sessionManager: SessionManager,
    private val chatApi: ChatApi,
    private val okHttpClient: OkHttpClient // Inject generic client or use a specific one
) {
    // Basic heuristic to get WS URL from HTTP URL. 
    // In a real app, inject this or from BuildConfig.
    // Assuming ApiClient.BASE_URL concept or we just hardcode/derive.
    // Let's use a hardcoded value for dev or strictly derive from a known constant if available.
    // Since we don't have ApiClient.BASE_URL easily accessbile here without the file, let's look at NetworkModule.
    // NetworkModule uses "http://10.0.2.2:8081". 
    private val wsBaseUrl = "ws://192.168.1.8:8081/ws-chat-raw"

    private val _incomingMessages = MutableSharedFlow<ChatMessageResponse>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val incomingMessages = _incomingMessages.asSharedFlow()

    private var webSocket: WebSocket? = null
    
    suspend fun getMyChats(): NetworkResult<List<ChatListResponse>> {
        return try {
            val response = chatApi.getMyChats()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error("Failed to fetch chats")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getMessages(activityId: Long): NetworkResult<List<ChatMessageResponse>> {
        return try {
            val response = chatApi.getMessages(activityId)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error("Failed to fetch messages")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Unknown error")
        }
    }

    fun connectToChat(activityId: Long) {
        val token = sessionManager.getToken() ?: return
        
        val request = Request.Builder()
            .url(wsBaseUrl)
            .build()
            
        // Close existing if open
        webSocket?.close(1000, "Switching chat")

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Send CONNECT frame
                val connectFrame = "CONNECT\naccept-version:1.2,1.1,1.0\nheart-beat:10000,10000\nAuthorization:Bearer $token\n\n\u0000"
                webSocket.send(connectFrame)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                if (text.startsWith("CONNECTED")) {
                    // Subscribe to activity topic
                    val subscribeFrame = "SUBSCRIBE\nid:sub-0\ndestination:/topic/activity.$activityId\n\n\u0000"
                    webSocket.send(subscribeFrame)
                } else if (text.startsWith("MESSAGE")) {
                    // Parse message body
                    val bodyIndex = text.indexOf("\n\n")
                    if (bodyIndex != -1) {
                        // Extract body, remove trailing null byte
                        val body = text.substring(bodyIndex + 2).replace("\u0000", "").trim()
                        if (body.isNotEmpty()) {
                            try {
                                val json = JSONObject(body)
                                val message = ChatMessageResponse(
                                    id = json.getLong("id"),
                                    activityId = json.getLong("activityId"),
                                    profileId = json.getLong("profileId"),
                                    profileName = json.getString("profileName"),
                                    profileAvatar = json.optString("profileAvatar", null),
                                    content = json.getString("content"),
                                    createdAt = json.getString("createdAt")
                                )
                                _incomingMessages.tryEmit(message)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }
        })
    }

    fun sendMessage(activityId: Long, profileId: Long, content: String) {
        val apiPath = "/app/chat/$activityId"
        val jsonContent = JSONObject().apply {
            put("profileId", profileId)
            put("content", content)
        }
        val sendFrame = "SEND\ndestination:$apiPath\ncontent-type:application/json\n\n$jsonContent\u0000"
        webSocket?.send(sendFrame)
    }

    fun disconnect() {
        webSocket?.close(1000, "Leaving chat")
        webSocket = null
    }
}
