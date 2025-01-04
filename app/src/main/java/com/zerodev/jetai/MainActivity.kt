package com.zerodev.jetai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.zerodev.jetai.ui.theme.JetAITheme
import com.zerodev.jetai.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetAITheme {
                val chatViewmodel = hiltViewModel<ChatViewModel>()
                JetAIApp(viewModel = chatViewmodel)
            }
        }
    }
}