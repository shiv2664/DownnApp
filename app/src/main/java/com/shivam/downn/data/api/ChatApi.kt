package com.shivam.downn.data.api

import com.shivam.downn.data.models.ChatListResponse
import com.shivam.downn.data.models.ChatMessageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatApi {
    @GET("api/v1/chats")
    suspend fun getMyChats(): Response<List<ChatListResponse>>

    @GET("api/v1/activities/{activityId}/messages")
    suspend fun getMessages(@Path("activityId") activityId: Long): Response<List<ChatMessageResponse>>
}
