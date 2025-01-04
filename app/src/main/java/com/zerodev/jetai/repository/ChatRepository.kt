package com.zerodev.jetai.repository

import com.zerodev.jetai.model.Chat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(messageList: List<Chat>, message: String): Flow<Chat>
}