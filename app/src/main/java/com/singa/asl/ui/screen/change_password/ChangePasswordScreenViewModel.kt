package com.singa.asl.ui.screen.change_password

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ChangePasswordScreenViewModel : ViewModel() {
    private val _isUpdatePasswordLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isUpdatePasswordLoading: MutableStateFlow<Boolean> get() = _isUpdatePasswordLoading

    fun setUpdatePasswordLoading(isLoading: Boolean) {
        _isUpdatePasswordLoading.value = isLoading
    }
}