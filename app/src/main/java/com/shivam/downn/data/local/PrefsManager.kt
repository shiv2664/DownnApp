package com.shivam.downn.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.shivam.downn.data.models.AuthResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class PrefsManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("downn_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveAuthResponse(response: AuthResponse) {
        prefs.edit().apply {
            putString("auth_data", gson.toJson(response))
            putString("token", response.token)
            putLong("userId", response.userId)
            apply()
        }
    }

    fun getAuthResponse(): AuthResponse? {
        val json = prefs.getString("auth_data", null) ?: return null
        return gson.fromJson(json, AuthResponse::class.java)
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    fun getUserId(): Long {
        return prefs.getLong("userId", 0)
    }

    fun saveActiveProfileId(id: Long) {
        prefs.edit().putLong("active_profile_id", id).apply()
    }

    fun getActiveProfileId(): Long {
        return prefs.getLong("active_profile_id", -1L)
    }

    fun clear() {
        prefs.edit { clear() }
    }
}
