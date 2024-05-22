package com.singa.asl.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.asl.common.ValidationState
import com.singa.asl.utils.FormValidators
import com.singa.core.data.Resource
import com.singa.core.domain.model.User
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainAppViewModel(
    private val singaUseCase: SingaUseCase
) : ViewModel() {
    private val _authUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val authUser: MutableStateFlow<User?> get() = _authUser

    private val _isSecondLaunch: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSecondLaunch: MutableStateFlow<Boolean> get() = _isSecondLaunch

    private val _validationState = mutableStateOf(ValidationState().copy())
    val validationState: ValidationState get() = _validationState.value

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email: MutableStateFlow<String> get() = _email

    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> get() = _password

    init {
        checkSecondLaunch()
        getAuthUser()
    }

    private fun checkSecondLaunch() {
        viewModelScope.launch {
            singaUseCase.getIsSecondLaunch().collect {
                _isSecondLaunch.value = it
            }
        }
    }

    private fun getAuthUser() {
        viewModelScope.launch {
            singaUseCase.getMe().collect {
                when (it) {
                    is Resource.Success -> {
                        _authUser.value = it.data
                    }

                    is Resource.Error -> {
                        _authUser.value = null
                    }

                    is Resource.Loading -> {
                        _authUser.value = null
                    }

                    is Resource.ValidationError -> {
                        _authUser.value = null
                    }
                }
            }
        }
    }

    fun onChangeEmail(email: String) {
        _validationState.value = if (email.isBlank()) {
            validationState.copy(
                emailError = "Email is required"
            )
        } else if (!FormValidators.isEmailValid(email)) {
            validationState.copy(
                emailError = "Email is invalid"
            )
        } else {
            validationState.copy(emailError = null)
        }

        _email.value = email
    }

    fun onChangePassword(password: String) {
        _validationState.value = if (password.isBlank()) {
            validationState.copy(
                passwordError = "Password is required"
            )
        } else if (!FormValidators.isPasswordValid(password)) {
            validationState.copy(
                passwordError = "Password must be at least 8 characters"
            )
        } else {
            validationState.copy(passwordError = null)
        }

        _password.value = password
    }

    fun updateValidationState(validationState: ValidationState) {
        _validationState.value = validationState
    }
}