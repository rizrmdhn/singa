package com.singa.asl.ui.screen.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.data.Resource
import com.singa.core.domain.model.Conversation
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MessageScreenViewModel(
    private val singaUseCase: SingaUseCase,
): ViewModel() {
    private val _state: MutableStateFlow<Resource<List<Conversation>>> = MutableStateFlow(Resource.Loading())
    val state: MutableStateFlow<Resource<List<Conversation>>> get() = _state

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: MutableStateFlow<Boolean> get() = _isRefreshing

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

    fun deleteConversationNode(
        id: Int,
        showDialog: (String, String) -> Unit,
    ) {
        viewModelScope.launch {
            singaUseCase.deleteConversationNode(id).collect {
                when (it) {
                    is Resource.Loading -> {
                       _state.value = Resource.Loading()
                    }
                    is Resource.Success -> {
                        getConversations()
                    }
                    is Resource.Error -> {
                        showDialog("Error", it.message)
                    }

                    is Resource.Empty -> TODO()
                    is Resource.ValidationError -> TODO()
                }
            }
        }
    }

    fun refreshConversations() {
        viewModelScope.launch {
            _isRefreshing.value = true
            getConversations()
            _isRefreshing.value = false
        }
    }
}