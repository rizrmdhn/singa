package com.singa.asl.ui.screen.conversation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ConversationViewModel: ViewModel(){
    var textFromSpeech: String? by mutableStateOf(null)
}