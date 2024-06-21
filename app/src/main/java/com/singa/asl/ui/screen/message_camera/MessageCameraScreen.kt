package com.singa.asl.ui.screen.message_camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.singa.asl.R
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color2
import com.singa.asl.utils.ProgressFileUpload
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

@Composable
fun MessageCameraScreen(
    id: Int,
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    showDialog: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MessageCameraViewModel = koinViewModel()
) {
    val uploadInProgress by viewModel.uploadInProgress.collectAsState()

    MessageCameraContent(
        id = id,
        context = context,
        lifecycleOwner = lifecycleOwner,
        uploadInProgress = uploadInProgress,
        showDialog = showDialog,
        onNavigateBack = onNavigateBack,
        onUploadVideo = viewModel::uploadVideo
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MessageCameraContent(
    modifier: Modifier = Modifier,
    id: Int,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    uploadInProgress: Boolean,
    showDialog: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    onUploadVideo: (
        id: Int,
        uri: MultipartBody.Part,
        showDialog: (String, String) -> Unit,
        navigateBack: () -> Unit
    ) -> Unit
) {
    val previewView = remember {
        PreviewView(context)
    }
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
            )
        }
    }

    val recordPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    var recording by remember {
        mutableStateOf<Recording?>(null)
    }

    var isFrontCamera by remember {
        mutableStateOf(false)
    }

    val executors = Executors.newSingleThreadExecutor()

    var isRecording by remember {
        mutableStateOf(false)
    }

    var uploadProgress by remember { mutableIntStateOf(0) }


    fun recordVideo() {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val outputFile =
            File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "video_$timeStamp.mp4")


        @SuppressLint("MissingPermission")
        recording = cameraController.startRecording(
            FileOutputOptions.Builder(outputFile).build(),
            AudioConfig.create(true),
            executors,
        ) {
            when (it) {
                is VideoRecordEvent.Finalize -> {
                    if (it.hasError()) {
                        recording?.close()
                        recording = null
                    } else {
                        MainScope().launch {
                            val multipartBody = outputFile.let { file ->
                                val contentType = "video/*".toMediaTypeOrNull()
                                ProgressFileUpload(file, contentType) { progress ->
                                    uploadProgress = progress
                                }.let { upd ->
                                    MultipartBody.Part.createFormData("file", file.name, upd)
                                }
                            }


                            onUploadVideo(
                                id,
                                multipartBody,
                                showDialog,
                                onNavigateBack
                            )
                            recording?.close()
                            recording = null
                        }
                    }
                }
            }
        }
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
            isRecording = true
            recordVideo()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
    }

    LaunchedEffect(isFrontCamera) {
        if (isFrontCamera) {
            cameraController.bindToLifecycle(lifecycleOwner)
            cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            previewView.controller = cameraController
        } else {
            cameraController.bindToLifecycle(lifecycleOwner)
            cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            previewView.controller = cameraController
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = Color1
            )
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(
                    shape = RoundedCornerShape(20.dp)
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uploadInProgress && uploadProgress > 0) {
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                        .padding(16.dp),
                )
                CircularProgressIndicator(
                    progress = { uploadProgress / 100f },
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Uploading video...  $uploadProgress", color = Color.White)
            }
            if (isRecording) {
                Row(
                    modifier = Modifier
                        .absolutePadding(bottom = 16.dp)
                        .padding(16.dp)
                        .background(
                            color = Color1,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 3.dp,
                            color = Color2,
                            shape = MaterialTheme.shapes.medium
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(10.dp),
                        onClick = {
                            if (recording != null) {
                                recording?.stop()
                            }
                            isRecording = false
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.when_recording),
                            contentDescription = "Capture Image"
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        modifier = Modifier
                            .padding(10.dp),
                        onClick = {
                            isFrontCamera = !isFrontCamera
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_cameraswitch_24),
                            contentDescription = "switch camera"
                        )
                    }
                }

            } else {
                Row(
                    modifier = Modifier
                        .absolutePadding(bottom = 16.dp)
                        .padding(16.dp)
                        .background(
                            color = Color1,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 3.dp,
                            color = Color2,
                            shape = MaterialTheme.shapes.medium
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        enabled = !uploadInProgress,
                        modifier = Modifier
                            .padding(10.dp),
                        onClick = {
                            when {
                                recordPermissionState.hasPermission -> {
                                    isRecording = true
                                    recordVideo()
                                }

                                recordPermissionState.shouldShowRationale -> {
                                    Toast.makeText(
                                        context,
                                        "Permission denied",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {
                                    recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.fluent_video_16_filled),
                            contentDescription = "Capture Image"
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        modifier = Modifier
                            .padding(10.dp),
                        onClick = {
                            isFrontCamera = !isFrontCamera
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_cameraswitch_24),
                            contentDescription = "switch camera"
                        )
                    }
                }
            }
        }
    }
}
