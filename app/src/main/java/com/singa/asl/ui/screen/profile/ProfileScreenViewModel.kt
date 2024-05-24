package com.singa.asl.ui.screen.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ProfileScreenViewModel: ViewModel() {
    private val _alertDialogTitle: MutableStateFlow<String> = MutableStateFlow("")
    val alertDialogTitle: MutableStateFlow<String> get() = _alertDialogTitle

    private val _alertDialogMessage: MutableStateFlow<String> = MutableStateFlow("")
    val alertDialogMessage: MutableStateFlow<String> get() = _alertDialogMessage

    private val _alertDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val alertDialog: MutableStateFlow<Boolean> get() = _alertDialog


    fun showAlert(title: String, message: String) {
        _alertDialogTitle.value = title
        _alertDialogMessage.value = message
        _alertDialog.value = true
    }

    fun hideAlert() {
        _alertDialog.value = false
    }
}