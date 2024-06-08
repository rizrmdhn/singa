package com.singa.asl.ui.screen.conversation_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.data.Resource
import com.singa.core.domain.model.DetailVideoConversation
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversationDetailScreenViewModel(
    private val singaUseCase: SingaUseCase
): ViewModel(){
    private val _state: MutableStateFlow<Resource<DetailVideoConversation>> = MutableStateFlow(
        Resource.Loading())
    val state: StateFlow<Resource<DetailVideoConversation>> get() = _state



    fun getVideoDetailTranslations(
        translationId: Int,
        conversationId: Int
    ) {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            singaUseCase.getVideoConversationDetails(translationId, conversationId).collect {
                _state.value = it
            }
        }
    }
}