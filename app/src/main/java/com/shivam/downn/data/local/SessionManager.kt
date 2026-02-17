package com.shivam.downn.data.local

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val prefsManager: PrefsManager
) {
    private val _logoutEvent = MutableSharedFlow<String?>(replay = 0)
    val logoutEvent: SharedFlow<String?> = _logoutEvent.asSharedFlow()

    suspend fun logout(reason: String? = null) {
        prefsManager.clear()
        _logoutEvent.emit(reason)
    }

    fun getToken(): String? {
        return prefsManager.getToken()
    }

    fun getProfileId(): Long {
        // Prefer active profile, fallback to user ID if needed, or -1
        val activeId = prefsManager.getActiveProfileId()
        return if (activeId != -1L) activeId else prefsManager.getUserId()
    }
}
