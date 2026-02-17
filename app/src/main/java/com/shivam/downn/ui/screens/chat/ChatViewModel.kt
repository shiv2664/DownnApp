package com.shivam.downn.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.local.SessionManager
import com.shivam.downn.data.models.ChatListResponse
import com.shivam.downn.data.models.ChatMessageResponse
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _chatList = MutableStateFlow<NetworkResult<List<ChatListResponse>>>(NetworkResult.Loading())
    val chatList: StateFlow<NetworkResult<List<ChatListResponse>>> = _chatList.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessageResponse>>(emptyList())
    val messages: StateFlow<List<ChatMessageResponse>> = _messages.asStateFlow()
    
    private val _currentProfileId = MutableStateFlow<Long?>(null)
    val currentProfileId: StateFlow<Long?> = _currentProfileId.asStateFlow()

    init {
        loadMyChats()
        viewModelScope.launch {
            _currentProfileId.value = sessionManager.getProfileId()
        }
        
        // Listen for incoming websocket messages
        viewModelScope.launch {
            chatRepository.incomingMessages.collect { newMessage ->
                _messages.value = _messages.value + newMessage
            }
        }
    }

    fun loadMyChats() {
        viewModelScope.launch(Dispatchers.IO) {
            _chatList.value = NetworkResult.Loading()
            _chatList.value = chatRepository.getMyChats()
        }
    }

    fun loadMessages(activityId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = chatRepository.getMessages(activityId)) {
                is NetworkResult.Success -> {
                    _messages.value = result.data ?: emptyList()
                }
                is NetworkResult.Error -> {
                    // Handle error (optional: show snackbar)
                }
                else -> {}
            }
        }
    }

    fun connectToChat(activityId: Long) {
        chatRepository.connectToChat(activityId)
        loadMessages(activityId)
    }
    
    fun disconnectFromChat() {
        chatRepository.disconnect()
        _messages.value = emptyList() // Clear messages on exit
    }

    fun sendMessage(activityId: Long, content: String) {
        val profileId = _currentProfileId.value ?: return
        if (content.isBlank()) return
        
        // Optimistic UI update (optional, but good for UX)
        // For now, let's rely on the socket echo or response to append
        chatRepository.sendMessage(activityId, profileId, content)
    }
}
