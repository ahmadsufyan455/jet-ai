package com.zerodev.jetai.repository

import com.zerodev.jetai.model.Chat
import com.zerodev.jetai.model.ChatSession
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun insertChatSession(chatSession: ChatSession)
    fun getAllChatSessions(): Flow<List<ChatSession>>
    suspend fun deleteChatSession(sessionId: String)
    suspend fun deleteAllSession()
    fun getChatsBySessionId(sessionId: String): Flow<List<Chat>>
    suspend fun insertChat(chat: Chat)
}