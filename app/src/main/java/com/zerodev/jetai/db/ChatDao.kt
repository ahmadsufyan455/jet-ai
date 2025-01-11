package com.zerodev.jetai.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zerodev.jetai.model.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getChatsBySessionId(sessionId: String): Flow<List<Chat>>

    @Insert
    suspend fun insertChat(chat: Chat)
}