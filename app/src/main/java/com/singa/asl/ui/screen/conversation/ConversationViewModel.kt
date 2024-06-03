package com.singa.asl.ui.screen.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.data.Resource
import com.singa.core.domain.model.ConversationNode
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val singaUseCase: SingaUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<Resource<List<ConversationNode>>> =
        MutableStateFlow(Resource.Loading())
    val state: MutableStateFlow<Resource<List<ConversationNode>>> get() = _state

    private val textMessage: MutableStateFlow<String> = MutableStateFlow("")
    val textMessageState: MutableStateFlow<String> get() = textMessage

    private val _isInputFocused: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInputFocused: MutableStateFlow<Boolean> get() = _isInputFocused

    fun getConversationNodes(
        id: Int,
    ) {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            singaUseCase.getConverstaionNodes(id).collect {
                _state.value = it
            }
        }
    }

    fun setTextMessage(text: String) {
        textMessage.value = text
    }

    fun setInputFocus(isFocused: Boolean) {
        _isInputFocused.value = isFocused
    }
}