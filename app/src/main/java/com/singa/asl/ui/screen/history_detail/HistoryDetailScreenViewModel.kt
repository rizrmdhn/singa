package com.singa.asl.ui.screen.history_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.data.Resource
import com.singa.core.domain.model.StaticTranslationDetail
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryDetailScreenViewModel(
    private val singaUseCase: SingaUseCase
): ViewModel(){
    private val _state: MutableStateFlow<Resource<StaticTranslationDetail>> = MutableStateFlow(
        Resource.Loading())
    val state: StateFlow<Resource<StaticTranslationDetail>> get() = _state



    fun getStaticDetailTranslations(id:Int) {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            singaUseCase.getStaticTranslationDetail(id).collect {
                _state.value = it
            }
        }
    }
}