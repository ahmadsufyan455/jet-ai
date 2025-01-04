package com.zerodev.jetai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerodev.jetai.model.Chat
import com.zerodev.jetai.model.ChatRoleEnum
import com.zerodev.jetai.repository.ChatRepository
import com.zerodev.jetai.ui.common.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatRepository: ChatRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UIState>(UIState.Success(emptyList()))
    val uiState: StateFlow<UIState> get() = _uiState

    fun sendMessage(message: String) {
        viewModelScope.launch {
            val currentMessages = (_uiState.value as? UIState.Success)?.messages.orEmpty()
            val updatedMessages = currentMessages + Chat(message, ChatRoleEnum.USER.value, false) + Chat(
                    "typing...",
                    ChatRoleEnum.MODEL.value,
                    true,
                    isTypingIndicator = true
                )

            _uiState.value = UIState.Loading

            try {
                _uiState.value = UIState.Success(updatedMessages)
                chatRepository.sendMessage(updatedMessages, message).collect { response ->
                    val finalMessages = updatedMessages.dropLast(1) + response.copy(isTypingIndicator = false)
                    _uiState.value = UIState.Success(finalMessages)
                }
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.localizedMessage}"
                _uiState.value = UIState.Error(errorMessage, updatedMessages.dropLast(1))
            }
        }
    }
}