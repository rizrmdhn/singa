package com.singa.asl.ui.screen.profile_detail

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ProfileDetailScreenViewModels : ViewModel() {
    private val _isUpdateProfileLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isUpdateProfileLoading: MutableStateFlow<Boolean> get() = _isUpdateProfileLoading

    private val _uri: MutableStateFlow<Uri> = MutableStateFlow(Uri.EMPTY)
    val uri: MutableStateFlow<Uri> get() = _uri

    fun setIsUpdateProfileLoading(isLoading: Boolean) {
        _isUpdateProfileLoading.value = isLoading
    }

    fun setUri(uri: Uri) {
        _uri.value = uri
    }
}