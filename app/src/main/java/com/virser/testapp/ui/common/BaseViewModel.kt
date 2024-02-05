package com.virser.testapp.ui.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

open class BaseViewModel : ViewModel() {

    protected val userMessage = MutableStateFlow<Int?>(null)

    fun snackbarMessageShown() {
        userMessage.value = null
    }

    fun showSnackbarMessage(message: Int) {
        userMessage.value = message
    }
}
