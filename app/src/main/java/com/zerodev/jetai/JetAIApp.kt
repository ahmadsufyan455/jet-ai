package com.zerodev.jetai

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import com.zerodev.jetai.model.Chat
import com.zerodev.jetai.repository.ChatRepositoryImpl
import com.zerodev.jetai.ui.components.AppBar
import com.zerodev.jetai.ui.components.ChatCard
import com.zerodev.jetai.ui.components.TextInput
import com.zerodev.jetai.ui.theme.JetAITheme
import com.zerodev.jetai.viewmodel.ChatViewModel

@Composable
fun JetAIApp(modifier: Modifier = Modifier, viewModel: ChatViewModel) {
    Scaffold(
        topBar = { AppBar() }
    ) { innerPadding ->
        val messageList by viewModel.messageList.collectAsState()
        val listState = rememberLazyListState()

        LaunchedEffect(messageList) {
            listState.animateScrollToItem(messageList.size - 1)
        }

        Column(
            modifier = modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
        ) {
            MessageList(messageList, modifier = Modifier.weight(1f), listState)
            TextInput(
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
            ) { message ->
                viewModel.sendMessage(message)
            }
        }
    }
}

@Composable
fun MessageList(messages: List<Chat>, modifier: Modifier = Modifier, listState: LazyListState) {
    if (messages.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ai),
                contentDescription = null,
                modifier = Modifier
                    .size(250.dp)
            )
            Text(
                text = "Looks like it's a bit quiet here... Start a conversation to bring this space to life!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center
                )
            )
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier
        ) {
            items(messages) {
                ChatCard(chat = it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JetAiAppPreview() {
    JetAITheme {
        JetAIApp(
            viewModel = ChatViewModel(
                chatRepository = ChatRepositoryImpl(
                    generativeModel = GenerativeModel(
                        modelName = "",
                        apiKey = ""
                    )
                )
            )
        )
    }
}