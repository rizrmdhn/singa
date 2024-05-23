package com.singa.asl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.data.Resource
import com.singa.core.domain.model.User
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val singaUseCase: SingaUseCase
): ViewModel() {
    private val _authUser: MutableStateFlow<Resource<User>> = MutableStateFlow(Resource.Loading())
    val authUser: MutableStateFlow<Resource<User>> get() = _authUser

    private val _isScreenReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isScreenReady: MutableStateFlow<Boolean> get() = _isScreenReady

    private val _initialScreen: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val initialScreen: MutableStateFlow<Boolean> get() = _initialScreen

    init {
        getAuthUser()
    }

    private fun getAuthUser() {
        viewModelScope.launch {
            singaUseCase.getMe().collect {
                _authUser.value = it
            }
        }
    }

    fun setScreenReady() {
        _isScreenReady.value = true
    }

    fun setScreenNotReady() {
        _isScreenReady.value = false
    }

}