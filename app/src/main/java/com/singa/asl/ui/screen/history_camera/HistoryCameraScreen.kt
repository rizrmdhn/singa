package com.singa.asl.ui.screen.history_camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import com.singa.asl.R
import com.singa.asl.ui.screen.history.HistoryScreenViewModel
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color2
import com.singa.asl.utils.Helpers.applyVideoEffects
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
fun HistoryCameraScreen(
    title: String,
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    showDialog: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: HistoryScreenViewModel = koinViewModel()
) {
    val uploadInProgress by viewModel.uploadInProgress.collectAsState()

    HistoryCameraContent(
        context = context,
        lifecycleOwner = lifecycleOwner,
        uploadInProgress = uploadInProgress,
        showDialog = showDialog,
        onNavigateBack = onNavigateBack,
        onUploadVideo = { file ->
            viewModel.createNewStaticTranslation(title, file, showDialog, onNavigateBack)
        }
    )
}

@Composable
fun HistoryCameraContent(
    modifier: Modifier = Modifier,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    uploadInProgress: Boolean,
    showDialog: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    onUploadVideo: (
        file: MultipartBody.Part,
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


    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            return@rememberLauncherForActivityResult
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

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
    var isProcessingVideo by remember { mutableStateOf(false) }
    var processingVideoProgress by remember { mutableIntStateOf(0) }

    fun recordVideo() {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val outputFile =
            File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "video_$timeStamp.mp4")
        val processedFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            "video_${timeStamp}_processed.mp4"
        )


        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

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
                            applyVideoEffects(outputFile, processedFile) { progress, status ->
                                isProcessingVideo = status
                                processingVideoProgress = progress
                            }


                            val multipartBody = processedFile.let { file ->
                                val contentType = "video/*".toMediaTypeOrNull()
                                ProgressFileUpload(file, contentType) { progress ->
                                    uploadProgress = progress
                                }.let { upd ->
                                    MultipartBody.Part.createFormData("file", file.name, upd)
                                }
                            }


                            onUploadVideo(
                                multipartBody,
                            )
                            recording?.close()
                            recording = null
                        }
                    }
                }
            }
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
            if (isProcessingVideo) {
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                        .padding(16.dp),
                )
                CircularProgressIndicator(
                    progress = { processingVideoProgress / 100f },
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Processing video...  $processingVideoProgress", color = Color.White)
            }
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
                            isRecording = true
                            recordVideo()
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
