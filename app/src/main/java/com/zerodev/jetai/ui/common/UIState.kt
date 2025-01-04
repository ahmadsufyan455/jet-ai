package com.zerodev.jetai.ui.common

import com.zerodev.jetai.model.Chat

sealed class UIState {
    object Loading : UIState()
    data class Success(val messages: List<Chat>) : UIState()
    data class Error(val message: String, val messages: List<Chat>) : UIState()
}