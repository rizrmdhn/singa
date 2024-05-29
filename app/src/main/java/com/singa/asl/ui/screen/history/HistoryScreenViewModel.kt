package com.singa.asl.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.data.Resource
import com.singa.core.domain.model.StaticTranslation
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryScreenViewModel(
    private val singaUseCase: SingaUseCase
): ViewModel() {
    private val _state: MutableStateFlow<Resource<List<StaticTranslation>>> = MutableStateFlow(Resource.Loading())
    val state: StateFlow<Resource<List<StaticTranslation>>> get() = _state

    init {
        getStaticTranslations()
    }

    private fun getStaticTranslations() {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            singaUseCase.getStaticTranslations().collect {
                _state.value = it
            }
        }
    }
}