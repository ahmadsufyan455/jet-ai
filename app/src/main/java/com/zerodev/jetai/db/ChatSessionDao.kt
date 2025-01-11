package com.zerodev.jetai.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.zerodev.jetai.model.ChatSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {
    @Query("SELECT * FROM chat_sessions ORDER BY createdAt ASC")
    fun getAllChatSessions(): Flow<List<ChatSession>>

    @Insert
    suspend fun insertChatSession(chatSession: ChatSession)

    @Delete
    suspend fun deleteChatSession(chatSession: ChatSession)
}