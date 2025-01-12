package com.zerodev.jetai.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zerodev.jetai.R
import com.zerodev.jetai.model.ChatSession
import com.zerodev.jetai.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawerContent(
    chatSessions: List<ChatSession>,
    chatViewModel: ChatViewModel,
    sessionId: String,
    drawerState: DrawerState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
    onDeleteSessions: () -> Unit
) {
    var newSessionId by remember { mutableStateOf("") }
    newSessionId = sessionId
    ModalDrawerSheet(modifier = modifier) {
        Text(
            stringResource(R.string.chat_history),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp)
                .weight(1f)
        ) {
            items(chatSessions) { chatSession ->
                ChatSessionItem(chatSession) {
                    chatViewModel.getChatsBySessionId(chatSession.sessionId)
                    newSessionId = chatSession.sessionId
                    scope.launch {
                        drawerState.close()
                    }
                }
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clickable(onClick = onDeleteSessions),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(R.string.clear_history))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = null
            )
        }
    }
}