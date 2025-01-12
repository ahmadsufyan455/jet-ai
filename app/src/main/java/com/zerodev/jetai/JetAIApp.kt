package com.zerodev.jetai

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import com.zerodev.jetai.injection.ChatModule.provideChatDao
import com.zerodev.jetai.injection.ChatModule.provideChatDatabase
import com.zerodev.jetai.injection.ChatModule.provideChatSessionDao
import com.zerodev.jetai.model.Chat
import com.zerodev.jetai.repository.ChatRepositoryImpl
import com.zerodev.jetai.repository.GenerativeModelRepositoryImpl
import com.zerodev.jetai.ui.common.UIState
import com.zerodev.jetai.ui.components.AppBar
import com.zerodev.jetai.ui.components.ChatCard
import com.zerodev.jetai.ui.components.ErrorView
import com.zerodev.jetai.ui.components.LoadingIndicator
import com.zerodev.jetai.ui.components.NavigationDrawerContent
import com.zerodev.jetai.ui.components.ShowConfirmationDialog
import com.zerodev.jetai.ui.components.TextInput
import com.zerodev.jetai.ui.theme.JetAITheme
import com.zerodev.jetai.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@Composable
fun JetAIApp(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel
) {
    var startNewChatDialog by remember { mutableStateOf(false) }
    var deleteAllSessionDialog by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val uiState by chatViewModel.uiState.collectAsState()
    val chatSessions by chatViewModel.chatSessions.collectAsState()
    var sessionId by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            AppBar(
                onMenuClick = {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                            chatViewModel.getAllChatSessions()
                        } else {
                            drawerState.close()
                        }
                    }
                },
                onNewChatClick = {
                    startNewChatDialog = true
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        // Clear history confirmation dialog
        ShowConfirmationDialog(
            titleRes = R.string.clear_history,
            messageRes = R.string.clear_history_message,
            showDialog = deleteAllSessionDialog,
            onConfirm = {
                chatViewModel.deleteAllSession()
                deleteAllSessionDialog = false
                scope.launch {
                    drawerState.close()
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.history_successfully_deleted),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
            },
            onDismiss = { deleteAllSessionDialog = false }
        )

        // Start new chat confirmation dialog
        ShowConfirmationDialog(
            titleRes = R.string.start_new_chat,
            messageRes = R.string.start_new_chat_message,
            showDialog = startNewChatDialog,
            onConfirm = {
                startNewChatDialog = false
                chatViewModel.resetSession()
            },
            onDismiss = { startNewChatDialog = false }
        )

        ModalNavigationDrawer(
            modifier = modifier.padding(innerPadding),
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                NavigationDrawerContent(
                    chatSessions = chatSessions,
                    chatViewModel = chatViewModel,
                    sessionId = sessionId,
                    drawerState = drawerState,
                    scope = scope,
                    modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp * 0.7f),
                    onDeleteSessions = { deleteAllSessionDialog = true }
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .consumeWindowInsets(innerPadding)
                        .imePadding()
                ) {
                    when (uiState) {
                        is UIState.Loading -> {
                            LoadingIndicator(modifier = Modifier.weight(1f))
                        }

                        is UIState.Success -> {
                            val messageList = (uiState as UIState.Success).messages
                            if (messageList.isNotEmpty()) {
                                LaunchedEffect(messageList) {
                                    listState.animateScrollToItem(messageList.size - 1)
                                }
                            }
                            MessageList(messageList, modifier = Modifier.weight(1f), listState)
                        }

                        is UIState.Error -> {
                            val errorMessage = (uiState as UIState.Error).message
                            ErrorView(
                                errorMessage,
                                (uiState as UIState.Error).messages,
                                modifier = Modifier.weight(1f)
                            ) { message ->
                                chatViewModel.sendMessage(message)
                            }
                        }
                    }
                    TextInput(
                        modifier = Modifier
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                    ) { message ->
                        chatViewModel.sendMessage(message)
                    }
                }
            }
        )
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
                text = stringResource(R.string.start_conversation),
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
            chatViewModel = ChatViewModel(
                chatRepository = ChatRepositoryImpl(
                    chatSessionDao = provideChatSessionDao(provideChatDatabase(LocalContext.current)),
                    chatDao = provideChatDao(provideChatDatabase(LocalContext.current))
                ),
                generativeModelRepository = GenerativeModelRepositoryImpl(
                    generativeModel = GenerativeModel(
                        modelName = "",
                        apiKey = ""
                    )
                )
            ),
        )
    }
}