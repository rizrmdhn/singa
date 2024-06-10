package com.singa.asl.ui.screen.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singa.core.data.Resource
import com.singa.core.domain.model.StaticTranslation
import com.singa.core.domain.usecase.SingaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class HistoryScreenViewModel(
    private val singaUseCase: SingaUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<Resource<List<StaticTranslation>>> =
        MutableStateFlow(Resource.Loading())
    val state: StateFlow<Resource<List<StaticTranslation>>> get() = _state

    private val _uploadInProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uploadInProgress: MutableStateFlow<Boolean> get() = _uploadInProgress

    private val _createStaticStateIsLoading: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val createStaticStateIsLoading: MutableStateFlow<Boolean> get() = _createStaticStateIsLoading

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: MutableStateFlow<Boolean> get() = _isRefreshing

    init {
        getStaticTranslations()
    }

    private fun getStaticTranslations() {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            singaUseCase.getStaticTranslations().collect {
                _state.value = it
            }
        }
    }

    fun refreshStaticTranslations() {
        viewModelScope.launch {
            _isRefreshing.value = true
            getStaticTranslations()
            _isRefreshing.value = false
        }
    }

    fun createNewStaticTranslation(
        title: String,
        file: MultipartBody.Part,
        showDialog: (String, String) -> Unit,
        navigateBack: () -> Unit
    ) {
        viewModelScope.launch {
            _createStaticStateIsLoading.value = true
            _uploadInProgress.value = true
            singaUseCase.createNewStaticTranslation(title, file).collect {
                when (it) {
                    is Resource.Empty -> {
                        _createStaticStateIsLoading.value = false
                        _uploadInProgress.value = false
                    }

                    is Resource.Error -> {
                        _createStaticStateIsLoading.value = false
                        _uploadInProgress.value = false
                        showDialog("Error", it.message)
                    }

                    is Resource.Loading -> {
                        _createStaticStateIsLoading.value = true
                        _uploadInProgress.value = true
                    }

                    is Resource.Success -> {
                        _createStaticStateIsLoading.value = false
                        _uploadInProgress.value = false
                        navigateBack()
                    }

                    is Resource.ValidationError -> {
                        _createStaticStateIsLoading.value = false
                        _uploadInProgress.value = false
                        showDialog("Error", it.errors.toString())
                    }
                }

            }
        }
    }

    fun deleteStaticTranslation(
        id: Int,
        showDialog: (String, String) -> Unit
    ) {
        viewModelScope.launch {
            singaUseCase.deleteStaticTranslation(id).collect {
                when (it) {
                    is Resource.Empty -> {

                    }

                    is Resource.Error -> {
                        showDialog("Error", it.message)
                    }

                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        getStaticTranslations()
                    }

                    is Resource.ValidationError -> {

                    }
                }
            }
        }
    }
}