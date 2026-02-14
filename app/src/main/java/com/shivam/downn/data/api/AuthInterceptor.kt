package com.shivam.downn.data.api

import com.shivam.downn.data.local.PrefsManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val prefsManager: PrefsManager,
    private val sessionManager: com.shivam.downn.data.local.SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        val token = prefsManager.getToken()
        
        if (token != null) {
            request.addHeader("Authorization", "Bearer $token")
        }
        
        val response = chain.proceed(request.build())
        
        if (response.code == 401) {
            // Token expired or invalid
            kotlinx.coroutines.runBlocking {
                sessionManager.logout("Session expired, please login again.")
            }
        }
        
        return response
    }
}
