package com.shivam.downn.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val navigationEventBus: NavigationEventBus
) : ViewModel() {

    fun onBottomNavClick(route: String) {
        viewModelScope.launch {
            navigationEventBus.emitEvent(route)
        }
    }
}
