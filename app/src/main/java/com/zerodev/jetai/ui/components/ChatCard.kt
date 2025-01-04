package com.zerodev.jetai.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zerodev.jetai.model.Chat
import com.zerodev.jetai.ui.theme.JetAITheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun ChatCard(chat: Chat, modifier: Modifier = Modifier) {
    val cardColor = if (chat.direction) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .padding(start = if (!chat.direction) 80.dp else 0.dp),
        horizontalAlignment = if (chat.direction) Alignment.Start else Alignment.End
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            ),
            shape = if (chat.direction) RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 20.dp
            ) else RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 0.dp,
                bottomStart = 20.dp,
                bottomEnd = 20.dp
            ),
            modifier = Modifier.clickable {}
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (chat.isTypingIndicator) {
                    TypingIndicator( modifier = Modifier.padding(12.dp))
                } else {
                    MarkdownText(
                        modifier = Modifier.padding(12.dp),
                        markdown = chat.message,
                        isTextSelectable = true,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatCardPreview() {
    JetAITheme {
        ChatCard(chat = Chat("Hello", "User", false))
    }
}