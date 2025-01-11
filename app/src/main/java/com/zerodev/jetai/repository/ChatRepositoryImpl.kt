package com.zerodev.jetai.repository

import com.zerodev.jetai.db.ChatDao
import com.zerodev.jetai.db.ChatSessionDao
import com.zerodev.jetai.model.Chat
import com.zerodev.jetai.model.ChatSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatSessionDao: ChatSessionDao,
    private val chatDao: ChatDao
) : ChatRepository {
    override suspend fun insertChatSession(chatSession: ChatSession) {
        chatSessionDao.insertChatSession(chatSession)
    }

    override fun getAllChatSessions(): Flow<List<ChatSession>> {
        return chatSessionDao.getAllChatSessions()
    }

    override suspend fun deleteChatSession(chatSession: ChatSession) {
        chatSessionDao.deleteChatSession(chatSession)
    }

    override fun getChatsBySessionId(sessionId: String): Flow<List<Chat>> {
        return chatDao.getChatsBySessionId(sessionId)
    }

    override suspend fun insertChat(chat: Chat) {
        chatDao.insertChat(chat)
    }
}