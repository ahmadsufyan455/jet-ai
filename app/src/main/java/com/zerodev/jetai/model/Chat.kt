package com.zerodev.jetai.model

data class Chat(
    val message: String,
    val role: String,
    val direction: Boolean,
    val isTypingIndicator: Boolean = false
)

enum class ChatRoleEnum(val value: String) {
    USER("User"),
    MODEL("Model")
}
