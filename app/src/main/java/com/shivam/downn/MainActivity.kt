package com.shivam.downn

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.shivam.downn.data.local.PrefsManager
import com.shivam.downn.data.repository.AppSettingsRepository
import com.shivam.downn.ui.theme.DownnTheme
import com.shivam.downn.navigation.AppNavigation
import com.shivam.downn.data.repository.AuthRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var authRepository: AuthRepository
    @Inject
    lateinit var prefsManager: PrefsManager
    @Inject
    lateinit var sessionManager: com.shivam.downn.data.local.SessionManager
    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    private var isSettingsReady = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep splash screen visible until app settings are loaded
        splashScreen.setKeepOnScreenCondition { !isSettingsReady }

        // Fetch endpoint configuration from server
        lifecycleScope.launch {
            val success = appSettingsRepository.refreshSettings()
            if (!success && !appSettingsRepository.hasCachedSettings()) {
                // First launch and server unreachable — still proceed
                // (API calls will fail gracefully with error messages)
                android.util.Log.w("MainActivity", "No cached settings and server unreachable")
            }
            isSettingsReady = true
        }
        
        val startDestination = if (authRepository.isUserLoggedIn()) "home" else "login"
        
        setContent {
            DownnTheme {
                AppNavigation(
                    startDestination = startDestination,
                    sessionManager = sessionManager
                )
            }
        }
    }
}
