package com.zerodev.jetai.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "chats",
    foreignKeys = [
        ForeignKey(
            entity = ChatSession::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Chat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sessionId: String,
    val message: String,
    val role: String,
    val direction: Boolean,
    val isTypingIndicator: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ChatRoleEnum(val value: String) {
    USER("User"),
    MODEL("Model")
}
