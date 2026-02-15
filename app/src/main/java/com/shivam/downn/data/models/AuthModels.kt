package com.shivam.downn.data.models

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String? = null
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("type") val type: SocialType = SocialType.PERSONAL,
    @SerializedName("role") val role: String? = null
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("userId") val userId: Long,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String
)

data class LogoutResponse(
    @SerializedName("message") val message: String
)
