package com.zerodev.jetai.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.zerodev.jetai.model.Chat
import com.zerodev.jetai.model.ChatRoleEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class GenerativeModelRepositoryImpl @Inject constructor(private val generativeModel: GenerativeModel) :
    GenerativeModelRepository {
    override suspend fun sendMessage(
        sessionId: String,
        messageList: List<Chat>,
        message: String
    ): Flow<Chat> {
        return flow {
            val history = messageList.map { chat ->
                content(chat.role) { text(chat.message) }
            }
            try {
                val response = withContext(Dispatchers.IO) {
                    val chat = generativeModel.startChat(history)
                    chat.sendMessage(message)
                }
                emit(
                    Chat(
                        sessionId = sessionId,
                        message = response.text.orEmpty(),
                        role = ChatRoleEnum.MODEL.value,
                        direction = true
                    )
                )
            } catch (e: Exception) {
                emit(
                    Chat(
                        sessionId = sessionId,
                        message = "Error: ${e.localizedMessage}",
                        role = ChatRoleEnum.MODEL.value,
                        direction = true
                    )
                )
            }
        }
    }
}