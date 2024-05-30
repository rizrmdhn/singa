package com.singa.asl.ui.screen.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.data.Resource
import com.singa.core.domain.model.Conversation
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MessageScreenViewModel(
    private val singaUseCase: SingaUseCase,
): ViewModel() {
    private val _state: MutableStateFlow<Resource<List<Conversation>>> = MutableStateFlow(Resource.Loading())
    val state: MutableStateFlow<Resource<List<Conversation>>> get() = _state

    init {
        getConversations()
    }

    private fun getConversations() {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            singaUseCase.getConversations().collect {
                _state.value = it
            }
        }
    }
}