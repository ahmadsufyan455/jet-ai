package com.zerodev.jetai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerodev.jetai.model.Chat
import com.zerodev.jetai.model.ChatRoleEnum
import com.zerodev.jetai.model.ChatSession
import com.zerodev.jetai.repository.ChatRepository
import com.zerodev.jetai.repository.GenerativeModelRepository
import com.zerodev.jetai.ui.common.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val generativeModelRepository: GenerativeModelRepository
) :
    ViewModel() {
    private val _chatSessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val chatSessions: StateFlow<List<ChatSession>> = _chatSessions

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private val _uiState = MutableStateFlow<UIState>(UIState.Success(emptyList()))
    val uiState: StateFlow<UIState> get() = _uiState

    private var currentSessionId: String? = null

    fun sendMessage(message: String) {
        viewModelScope.launch {
            if (currentSessionId == null) {
                createNewChatSession(message)
            }
            currentSessionId?.let { sessionId ->
                val currentMessages = (_uiState.value as? UIState.Success)?.messages.orEmpty()
                val updatedMessages = currentMessages + Chat(
                    sessionId = sessionId,
                    message = message,
                    role = ChatRoleEnum.USER.value,
                    direction = false
                ) + Chat(
                    sessionId = sessionId,
                    message = "typing...",
                    role = ChatRoleEnum.MODEL.value,
                    direction = true,
                    isTypingIndicator = true
                )

                insertChat(
                    Chat(
                        sessionId = sessionId,
                        message = message,
                        role = ChatRoleEnum.USER.value,
                        direction = false
                    )
                )

                _uiState.value = UIState.Loading

                try {
                    _uiState.value = UIState.Success(updatedMessages)
                    generativeModelRepository.sendMessage(sessionId, updatedMessages, message)
                        .collect { response ->
                            val finalMessages =
                                updatedMessages.dropLast(1) + response.copy(isTypingIndicator = false)
                            _uiState.value = UIState.Success(finalMessages)
                            insertChat(
                                Chat(
                                    sessionId = sessionId,
                                    message = response.message,
                                    role = ChatRoleEnum.MODEL.value,
                                    direction = true
                                )
                            )
                        }
                } catch (e: Exception) {
                    val errorMessage = "Error: ${e.localizedMessage}"
                    _uiState.value = UIState.Error(errorMessage, updatedMessages.dropLast(1))
                }
            }
        }
    }

    fun getAllChatSessions() {
        viewModelScope.launch(Dispatchers.IO) {
            val sessions = chatRepository.getAllChatSessions().first()
            _chatSessions.value = sessions
        }
    }

    fun getChatsBySessionId(sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val chatsSession = chatRepository.getChatsBySessionId(sessionId).first()
            _chats.value = chatsSession
            _uiState.value = UIState.Success(chats.value)
            currentSessionId = sessionId
        }
    }

    private fun insertChatSession(chatSession: ChatSession) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.insertChatSession(chatSession)
        }
    }

    private fun insertChat(chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.insertChat(chat)
        }
    }

    fun deleteChatSession(sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.deleteChatSession(sessionId)
        }
    }

    fun deleteAllSession() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                chatRepository.deleteAllSession()
                withContext(Dispatchers.Main) {
                    _uiState.value = UIState.Success(emptyList())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = UIState.Error("${e.message}", _chats.value)
                }
            }
        }
    }

    private fun createNewChatSession(sessionTitle: String) {
        val newSessionId = UUID.randomUUID().toString()
        val newChatSession = ChatSession(
            sessionId = newSessionId,
            title = sessionTitle,
            createdAt = System.currentTimeMillis()
        )
        insertChatSession(newChatSession)
        currentSessionId = newSessionId
    }

    fun resetSession() {
        currentSessionId = null
        _uiState.value = UIState.Success(emptyList())
    }
}