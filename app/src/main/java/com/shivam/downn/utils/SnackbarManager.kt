package com.shivam.downn.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Global snackbar manager for showing error/info messages across the app.
 * Allows any ViewModel or component to emit snackbar messages.
 */
object SnackbarManager {

    data class SnackbarMessage(
        val message: String,
        val isError: Boolean = false,
        val actionLabel: String? = null
    )

    private val _messages = MutableSharedFlow<SnackbarMessage>(extraBufferCapacity = 5)
    val messages = _messages.asSharedFlow()

    fun showMessage(message: String) {
        _messages.tryEmit(SnackbarMessage(message))
    }

    fun showError(message: String, actionLabel: String? = "Dismiss") {
        _messages.tryEmit(SnackbarMessage(message, isError = true, actionLabel = actionLabel))
    }
}
