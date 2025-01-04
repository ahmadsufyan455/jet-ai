package com.zerodev.jetai.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zerodev.jetai.model.Chat

@Composable
fun ErrorView(
    errorMessage: String,
    messages: List<Chat>,
    modifier: Modifier = Modifier,
    onRetry: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (messages.isNotEmpty()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(messages) { chat ->
                    ChatCard(chat = chat)
                }
            }
        }
        Button(onClick = { onRetry("Retry message") }) {
            Text(text = "Retry")
        }
    }
}