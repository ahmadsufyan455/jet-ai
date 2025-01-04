package com.zerodev.jetai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerodev.jetai.model.Chat
import com.zerodev.jetai.model.ChatRoleEnum
import com.zerodev.jetai.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatRepository: ChatRepository) : ViewModel() {
    private val _messageList = MutableStateFlow<List<Chat>>(emptyList())
    val messageList: StateFlow<List<Chat>> get() = _messageList

    fun sendMessage(message: String) {
        viewModelScope.launch {
            val updatedMessageList = _messageList.value.toMutableList()
            updatedMessageList.add(Chat(message, ChatRoleEnum.USER.value, false))
            updatedMessageList.add(Chat("Typing...", ChatRoleEnum.MODEL.value, true, isTypingIndicator = true))
            _messageList.value = updatedMessageList

            chatRepository.sendMessage(updatedMessageList, message)
                .collect { response ->
                    val newMessageList = updatedMessageList.toMutableList()
                    newMessageList[newMessageList.lastIndex] = response.copy(isTypingIndicator = false)
                    _messageList.value = newMessageList
                }
        }
    }
}