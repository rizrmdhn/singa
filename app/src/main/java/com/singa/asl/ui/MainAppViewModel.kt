package com.singa.asl.ui

import android.util.Log
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

    private val _loginIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loginIsLoading get() = _loginIsLoading.value

    private val _registerIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val registerIsLoading get() = _registerIsLoading.value

    private val _validationState = mutableStateOf(ValidationState().copy())
    val validationState: ValidationState get() = _validationState.value

    private val _name: MutableStateFlow<String> = MutableStateFlow("")
    val name: MutableStateFlow<String> get() = _name

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email: MutableStateFlow<String> get() = _email

    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> get() = _password

    private val _alertDialogTitle: MutableStateFlow<String> = MutableStateFlow("")
    val alertDialogTitle: MutableStateFlow<String> get() = _alertDialogTitle

    private val _alertDialogMessage: MutableStateFlow<String> = MutableStateFlow("")
    val alertDialogMessage: MutableStateFlow<String> get() = _alertDialogMessage

    private val _alertDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val alertDialog: MutableStateFlow<Boolean> get() = _alertDialog

    init {
        getAuthUser()
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

    fun onChangeName(name: String) {
        _validationState.value = if (name.isBlank()) {
            validationState.copy(
                nameError = "Name is required"
            )
        } else if (!FormValidators.nameLength(name)) {
            validationState.copy(
                nameError = "Name must be at least 3 characters"
            )
        } else if (!FormValidators.isNameValid(name)) {
            validationState.copy(
                nameError = "Name is invalid"
            )
        } else {
            validationState.copy(nameError = null)
        }

        _name.value = name
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

    fun onLogin(
        navigateToHome: () -> Unit,
    ) {
        if (email.value.isBlank() || password.value.isBlank()) {
            _validationState.value = validationState.copy(
                emailError = "Email is required",
                passwordError = "Password is required"
            )
            return
        }

        if (!FormValidators.isEmailValid(email.value)) {
            _validationState.value = validationState.copy(
                emailError = "Email is invalid"
            )
        }

        if (!FormValidators.isPasswordValid(password.value)) {
            _validationState.value = validationState.copy(
                passwordError = "Password must be at least 8 characters"
            )
        }

        if (validationState.emailError != null || validationState.passwordError != null) {
            return
        }

        viewModelScope.launch {
            _loginIsLoading.value = true
            singaUseCase.login(email.value, password.value).collect {
                when (it) {
                    is Resource.Success -> {
                        singaUseCase.saveAccessToken(it.data.accessToken)
                        singaUseCase.saveRefreshToken(it.data.refreshToken)
                        getAuthUser()
                        navigateToHome()
                        _loginIsLoading.value = false
                        cleanName()
                        cleanEmail()
                        cleanPassword()
                        cleanValidationState()
                    }

                    is Resource.Error -> {
                        _alertDialogTitle.value = "Error"
                        _alertDialogMessage.value = it.message
                        _alertDialog.value = true
                        _loginIsLoading.value = false
                    }

                    is Resource.Loading -> {
                        _loginIsLoading.value = true
                    }

                    is Resource.ValidationError -> {
                        _loginIsLoading.value = false
                        it.errors.forEach { (key, value) ->
                            when (key) {
                                "email" -> {
                                    _validationState.value = validationState.copy(
                                        emailError = value
                                    )
                                }

                                "password" -> {
                                    _validationState.value = validationState.copy(
                                        passwordError = value
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onRegister(
        navigateToLogin: () -> Unit
    ) {
        if (email.value.isBlank() || password.value.isBlank() || name.value.isBlank()) {
            _validationState.value = validationState.copy(
                nameError = "Name is required",
                emailError = "Email is required",
                passwordError = "Password is required"
            )
            return
        }

        if (!FormValidators.isNameValid(name.value)) {
            _validationState.value = validationState.copy(
                nameError = "Name is invalid"
            )
        }

        if (!FormValidators.isEmailValid(email.value)) {
            _validationState.value = validationState.copy(
                emailError = "Email is invalid"
            )
        }

        if (!FormValidators.isPasswordValid(password.value)) {
            _validationState.value = validationState.copy(
                passwordError = "Password must be at least 8 characters"
            )
        }


        if (validationState.emailError != null || validationState.passwordError != null) {
            return
        }

        viewModelScope.launch {
            _registerIsLoading.value = true
            singaUseCase.register(name.value, email.value, password.value).collect {
                when (it) {
                    is Resource.Success -> {
                        navigateToLogin()
                        _registerIsLoading.value = false
                        _alertDialogTitle.value = "Success"
                        _alertDialogMessage.value = it.data
                        _alertDialog.value = true
                        cleanName()
                        cleanEmail()
                        cleanPassword()
                        cleanValidationState()
                    }

                    is Resource.Error -> {
                        _alertDialogTitle.value = "Error"
                        _alertDialogMessage.value = it.message
                        _alertDialog.value = true
                        _registerIsLoading.value = false
                    }

                    is Resource.Loading -> {
                        _registerIsLoading.value = true
                    }

                    is Resource.ValidationError -> {
                        _registerIsLoading.value = false
                        it.errors.forEach { (key, value) ->
                            when (key) {
                                "email" -> {
                                    _validationState.value = validationState.copy(
                                        emailError = value
                                    )
                                }

                                "password" -> {
                                    _validationState.value = validationState.copy(
                                        passwordError = value
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun dismissAlertDialog() {
        _alertDialog.value = false
    }

    fun cleanName() {
        _name.value = ""
    }

    fun cleanEmail() {
        _email.value = ""
    }

    fun cleanPassword() {
        _password.value = ""
    }

    fun cleanValidationState() {
        _validationState.value = ValidationState().copy()
    }
}