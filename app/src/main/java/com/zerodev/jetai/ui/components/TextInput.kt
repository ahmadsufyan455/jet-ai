package com.zerodev.jetai.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zerodev.jetai.R
import com.zerodev.jetai.ui.theme.JetAITheme


@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    onMessageSend: (String) -> Unit,
) {
    var message by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
    ) {
        TextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Type your message...") },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .padding(vertical = 4.dp),
            trailingIcon = {
                if (message.isEmpty()) {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.ic_mic),
                            contentDescription = null
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        )
        IconButton(onClick = {
            focusManager.clearFocus()
            if (message.isNotEmpty()) {
                onMessageSend(message)
                message = ""
            }
        }) {
            Icon(
                painter = painterResource(R.drawable.ic_send),
                contentDescription = null,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TextInputPreview() {
    JetAITheme {
        TextInput(modifier = Modifier.padding(8.dp)) {}
    }
}