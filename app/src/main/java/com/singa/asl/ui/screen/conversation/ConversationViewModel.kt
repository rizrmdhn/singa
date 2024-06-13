package com.singa.asl.ui.screen.conversation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.data.Resource
import com.singa.core.domain.model.ConversationNode
import com.singa.core.domain.usecase.SingaUseCase
import com.singa.core.utils.DataMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val singaUseCase: SingaUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<Resource<List<ConversationNode>>> =
        MutableStateFlow(Resource.Loading())
    val state: MutableStateFlow<Resource<List<ConversationNode>>> get() = _state

    private val _createConversationStateIsLoading: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val createConversationStateIsLoading: MutableStateFlow<Boolean> get() = _createConversationStateIsLoading

    private val _textMessage: MutableStateFlow<String> = MutableStateFlow("")
    val textMessageState: MutableStateFlow<String> get() = _textMessage

    private val _isInputFocused: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInputFocused: MutableStateFlow<Boolean> get() = _isInputFocused

    private val _selectedConversationNodes: MutableStateFlow<Set<Int>> =
        MutableStateFlow(emptySet())
    val selectedConversationNodes: MutableStateFlow<Set<Int>> get() = _selectedConversationNodes

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: MutableStateFlow<Boolean> get() = _isRefreshing

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
        _textMessage.value = text
    }

    fun setInputFocus(isFocused: Boolean) {
        _isInputFocused.value = isFocused
    }

    fun createConversation(
        title: String,
        navigateToConversation: (String) -> Unit
    ) {
        viewModelScope.launch {
            singaUseCase.createConversation(title).collect {
                _createConversationStateIsLoading.value = true
                when (it) {
                    is Resource.Loading -> {
                        _createConversationStateIsLoading.value = true
                    }

                    is Resource.Success -> {
                        _createConversationStateIsLoading.value = false
                        navigateToConversation(it.data.id.toString())
                    }

                    is Resource.Error -> {
                        _createConversationStateIsLoading.value = false
                    }

                    is Resource.Empty -> {
                        Log.e("ConversationViewModel", "createConversation: empty")
                    }

                    is Resource.ValidationError -> {
                        Log.e("ConversationViewModel", "createConversation: ${it.errors}")
                    }
                }
            }
        }
    }

    fun createSpeechConversation(
        conversationId: Int,
    ) {
        viewModelScope.launch {
            singaUseCase.createNewSpeechConversation(textMessageState.value, conversationId)
                .collect {
                    _createConversationStateIsLoading.value = true
                    when (it) {
                        is Resource.Loading -> {
                            _createConversationStateIsLoading.value = true
                        }

                        is Resource.Success -> {
                            _createConversationStateIsLoading.value = false
                            val mapData =
                                DataMapper.mapSpeechConversationToConversationNode(it.data)
                            _state.value = Resource.Success(_state.value.let { resource ->
                                if (resource is Resource.Success) {
                                    resource.data.toMutableList().apply {
                                        add(mapData)
                                    }
                                } else {
                                    listOf(mapData)
                                }
                            })
                        }

                        is Resource.Error -> {
                            _createConversationStateIsLoading.value = false
                        }

                        is Resource.Empty -> {
                            Log.e("ConversationViewModel", "createConversation: empty")
                        }

                        is Resource.ValidationError -> {
                            Log.e("ConversationViewModel", "createConversation: ${it.errors}")
                        }
                    }
                }
        }
    }

    fun bulkDeleteConversationNode(
        showDialog: (String, String) -> Unit,
    ) {
        viewModelScope.launch {
            singaUseCase.bulkDeleteConversationNode(selectedConversationNodes.value).collect {
                when (it) {
                    is Resource.Success -> {
                        _state.value = Resource.Success(_state.value.let { resource ->
                            if (resource is Resource.Success) {
                                resource.data.filter { conversationNode ->
                                    !selectedConversationNodes.value.contains(conversationNode.id)
                                }
                            } else {
                                emptyList()
                            }
                        })
                        emptySelection()
                    }

                    is Resource.Error -> {
                        Log.e("ConversationViewModel", "bulkDeleteConversationNode: ${it.message}")
                        showDialog("Error", it.message)
                    }

                    is Resource.Empty -> {
                        Log.e("ConversationViewModel", "bulkDeleteConversationNode: empty")
                    }

                    is Resource.ValidationError -> {
                        Log.e("ConversationViewModel", "bulkDeleteConversationNode: ${it.errors}")
                    }

                    is Resource.Loading -> {
                        Log.e("ConversationViewModel", "bulkDeleteConversationNode: loading")
                    }
                }
            }
        }
    }

    fun toggleSelection(conversationNodeId: Int) {
        val currentSelection = _selectedConversationNodes.value
        _selectedConversationNodes.value = if (currentSelection.contains(conversationNodeId)) {
            currentSelection - conversationNodeId
        } else {
            currentSelection + conversationNodeId
        }
    }

    fun emptySelection() {
        _selectedConversationNodes.value = emptySet()
    }

    fun updateConversationState() {
        viewModelScope.launch {
            _state.value = Resource.Success(_state.value.let { resource ->
                if (resource is Resource.Success) {
                    resource.data.map { conversationNode ->
                        conversationNode.copy(
                            isSelected = selectedConversationNodes.value.contains(
                                conversationNode.id
                            )
                        )
                    }
                } else {
                    emptyList()
                }
            })
        }
    }

    fun refreshConversations(id: Int) {
        viewModelScope.launch {
            _isRefreshing.value = true
            getConversationNodes(id)
            _isRefreshing.value = false
        }
    }
}