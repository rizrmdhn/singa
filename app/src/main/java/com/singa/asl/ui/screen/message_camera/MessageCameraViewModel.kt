package com.singa.asl.ui.screen.message_camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.data.Resource
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody


class MessageCameraViewModel(
    private val singaUseCase: SingaUseCase,
) : ViewModel() {
    private val _uploadInProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uploadInProgress: MutableStateFlow<Boolean> get() = _uploadInProgress

    fun uploadVideo(
        id: Int,
        file: MultipartBody.Part,
        showDialog: (String, String) -> Unit,
        navigateBack: () -> Unit
    ) {
        viewModelScope.launch {
            singaUseCase.createNewVideoConversation(id, file).collect {
                when (it) {
                    is Resource.Empty -> {
                        _uploadInProgress.value = false
                        showDialog("Error", "An error occurred")
                    }

                    is Resource.Error -> {
                        _uploadInProgress.value = false
                        showDialog("Error", it.message ?: "An error occurred")
                    }

                    is Resource.Loading -> {
                        _uploadInProgress.value = true
                    }

                    is Resource.Success -> {
                        _uploadInProgress.value = false
                        navigateBack()
                    }

                    is Resource.ValidationError -> {
                        _uploadInProgress.value = false
                        showDialog("Error", it.errors.joinToString { msg -> msg.message })
                    }
                }
            }
        }
    }
}