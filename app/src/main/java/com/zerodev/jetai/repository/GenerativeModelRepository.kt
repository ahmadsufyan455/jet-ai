package com.zerodev.jetai.repository

import com.zerodev.jetai.model.Chat
import kotlinx.coroutines.flow.Flow

interface GenerativeModelRepository {
    suspend fun sendMessage(
        sessionId: String,
        messageList: List<Chat>,
        message: String
    ): Flow<Chat>
}