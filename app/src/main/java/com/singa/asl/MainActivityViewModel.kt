package com.singa.asl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.asl.common.ValidationState
import com.singa.core.data.Resource
import com.singa.core.domain.model.User
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

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

    private val _alertDialogTitle: MutableStateFlow<String> = MutableStateFlow("")
    val alertDialogTitle: MutableStateFlow<String> get() = _alertDialogTitle

    private val _alertDialogMessage: MutableStateFlow<String> = MutableStateFlow("")
    val alertDialogMessage: MutableStateFlow<String> get() = _alertDialogMessage

    private val _alertDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val alertDialog: MutableStateFlow<Boolean> get() = _alertDialog



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

    fun saveAccessToken(accessToken: String) {
        viewModelScope.launch {
            singaUseCase.saveAccessToken(accessToken)
        }
    }

    fun saveRefreshToken(refreshToken: String) {
        viewModelScope.launch {
            singaUseCase.saveRefreshToken(refreshToken)
        }
    }

    fun getAuthUser() {
        viewModelScope.launch {
            singaUseCase.getMe().collect {
                _authUser.value = it
            }
        }
    }


    fun logout(
        navigateToLogin: () -> Unit
    ) {
        viewModelScope.launch {
            singaUseCase.logout().collect {
                when (it) {
                    is Resource.Success -> {
                        removeAccessToken()
                        removeRefreshToken()
                        showAlert("Success", "Logout success")
                        _logoutIsLoading.value = false
                        _authUser.value = Resource.Empty()
                        navigateToLogin()
                    }

                    is Resource.Empty -> {
                        showAlert("Error", "Something went wrong")
                        _logoutIsLoading.value = false
                    }

                    is Resource.Error -> {
                        Log.e("MainActivityViewModel", "Error: $it.")
                        showAlert("Error", it.message.ifBlank { "Something went wrong" })
                        _logoutIsLoading.value = false
                    }

                    is Resource.Loading -> {
                        _logoutIsLoading.value = true
                    }

                    is Resource.ValidationError -> {
                        showAlert("Error", "Something went wrong")
                        _logoutIsLoading.value = false
                        return@collect
                    }
                }
            }
        }
    }

    fun updateUser(
        uri: MultipartBody.Part?,
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        isSignUser: Boolean,
        updateValidationState: (validationState: ValidationState) -> Unit,
        clearChangePasswordForm: () -> Unit,
        setUpdateIsLoading: (status: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            singaUseCase.updateMe(
                name = name,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                avatar = uri,
                isSignUser = isSignUser
            ).collect {
                when (it) {
                    is Resource.Success -> {
                        setUpdateIsLoading(false)
                        _authUser.value = Resource.Success(it.data)
                        if (password.isNotBlank() && confirmPassword.isNotBlank()) {
                            clearChangePasswordForm()
                        }
                        showAlert("Success", "Update success")
                    }
                    is Resource.Empty -> {
                        setUpdateIsLoading(false)
                        showAlert("Error", "Something went wrong")
                    }
                    is Resource.Error -> {
                        setUpdateIsLoading(false)
                        showAlert("Error", it.message)
                    }
                    is Resource.Loading -> {
                        setUpdateIsLoading(true)
                    }
                    is Resource.ValidationError -> {
                        setUpdateIsLoading(false)
                        it.errors.forEach { (msg, _, field) ->
                            when (field) {
                                "name" -> {
                                    updateValidationState(ValidationState(nameError = msg))
                                }
                                "password" -> {
                                    updateValidationState(ValidationState(passwordError = msg))
                                }
                                "avatar" -> {
                                    showAlert("Error", msg)
                                }
                                "isSignUser" -> {
                                    showAlert("Error", msg)
                                }
                            }
                        }
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

    fun showAlert(title: String, message: String) {
        _alertDialogTitle.value = title
        _alertDialogMessage.value = message
        _alertDialog.value = true
    }

    fun hideAlert() {
        _alertDialogTitle.value = ""
        _alertDialogMessage.value = ""
        _alertDialog.value = false
    }
}