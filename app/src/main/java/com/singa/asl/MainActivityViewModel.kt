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
) : ViewModel() {
    private val _authUser: MutableStateFlow<Resource<User>> = MutableStateFlow(Resource.Loading())
    val authUser: MutableStateFlow<Resource<User>> get() = _authUser

    private val _isSecondLaunch: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSecondLaunch: MutableStateFlow<Boolean> get() = _isSecondLaunch

    private val _isScreenReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isScreenReady: MutableStateFlow<Boolean> get() = _isScreenReady

    private val _logoutIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val logoutIsLoading: MutableStateFlow<Boolean> get() = _logoutIsLoading


    init {
        getAuthUser()
        checkSecondLaunch()
    }



    private fun checkSecondLaunch() {
        viewModelScope.launch {
            singaUseCase.getIsSecondLaunch().collect {
                _isSecondLaunch.value = it
            }
        }
    }

    private fun removeAccessToken() {
        viewModelScope.launch {
            singaUseCase.removeAccessToken()
        }
    }

    private fun removeRefreshToken() {
        viewModelScope.launch {
            singaUseCase.removeRefreshToken()
        }
    }

    private fun getAuthUser() {
        viewModelScope.launch {
            singaUseCase.getMe().collect {
                _authUser.value = it
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            removeAccessToken()
            removeRefreshToken()
            singaUseCase.logout().collect {
                when (it) {
                    is Resource.Success -> {
                        _logoutIsLoading.value = false
                        _authUser.value = Resource.Loading()
                    }

                    is Resource.Error -> {
                        _logoutIsLoading.value = false
                    }

                    is Resource.Loading -> {
                        _logoutIsLoading.value = true
                    }

                    is Resource.ValidationError -> {
                        _logoutIsLoading.value = false
                        return@collect
                    }
                }
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