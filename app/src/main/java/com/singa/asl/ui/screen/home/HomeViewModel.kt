package com.singa.asl.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.data.Resource
import com.singa.core.domain.model.Articles
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val singaUseCase: SingaUseCase
):ViewModel() {

    private val _state = MutableStateFlow<Resource<List<Articles>>>(Resource.Loading())
    val state: StateFlow<Resource<List<Articles>>> get() = _state

    init {
        getArticles()
    }

    private fun getArticles() {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            singaUseCase.getArticles().collect {
                _state.value = it
            }
        }
    }
}