package com.singa.asl.ui

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.asl.common.ValidationState
import com.singa.asl.utils.FormValidators
import com.singa.core.data.Resource
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainAppViewModel(
    private val singaUseCase: SingaUseCase
) : ViewModel() {

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

    private val _isSignUser: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSignUser: MutableStateFlow<Boolean> get() = _isSignUser


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

    fun onSignUser() {
        _isSignUser.value = !_isSignUser.value
    }

    fun onLogin(
        getAuthUser: () -> Unit,
        navigateToHome: () -> Unit,
        showDialog: (String, String) -> Unit
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
                        showDialog("Success", "Login success")
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

                    is Resource.Empty -> {
                        showDialog("Error", "Something went wrong")
                        _loginIsLoading.value = false
                    }

                    is Resource.Error -> {
                        showDialog("Error", it.message)
                        _loginIsLoading.value = false
                    }

                    is Resource.Loading -> {
                        _loginIsLoading.value = true
                    }

                    is Resource.ValidationError -> {
                        _loginIsLoading.value = false
                        it.errors.forEach { (msg, _, field) ->
                            when (field) {
                                "email" -> {
                                    _validationState.value = validationState.copy(
                                        emailError = msg
                                    )
                                }

                                "password" -> {
                                    _validationState.value = validationState.copy(
                                        passwordError = msg
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
        navigateToLogin: () -> Unit,
        showDialog: (String, String) -> Unit
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
                        showDialog("Success", "Register success")
                        cleanName()
                        cleanEmail()
                        cleanPassword()
                        cleanValidationState()
                    }

                    is Resource.Empty -> {
                        showDialog("Error", "Something went wrong")
                        _registerIsLoading.value = false
                    }

                    is Resource.Error -> {
                        showDialog("Error", it.message)
                        _registerIsLoading.value = false
                    }

                    is Resource.Loading -> {
                        _registerIsLoading.value = true
                    }

                    is Resource.ValidationError -> {
                        _registerIsLoading.value = false
                        it.errors.forEach { (msg, _, field) ->
                            when (field) {
                                "name" -> {
                                    _validationState.value = validationState.copy(
                                        nameError = msg
                                    )
                                }

                                "email" -> {
                                    _validationState.value = validationState.copy(
                                        emailError = msg
                                    )
                                }

                                "password" -> {
                                    _validationState.value = validationState.copy(
                                        passwordError = msg
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
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

    fun cleanSignUser() {
        _isSignUser.value = false
    }

    fun updateValidationState(validationState: ValidationState) {
        _validationState.value = validationState
    }

    fun cleanValidationState() {
        _validationState.value = ValidationState().copy()
    }
}