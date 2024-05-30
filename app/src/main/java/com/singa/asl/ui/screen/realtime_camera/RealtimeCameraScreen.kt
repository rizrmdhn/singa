package com.singa.asl.ui.screen.realtime_camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mediapipe.tasks.components.containers.Classifications
import com.singa.asl.R
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color2
import com.singa.asl.utils.Helpers
import com.singa.asl.utils.ImageClassifierHelper
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

@Composable
fun RealtimeCameraScreen(
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    RealtimeCameraContent(
        context,
        lifecycleOwner
    )
}

@Composable
fun RealtimeCameraContent(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    val previewView = remember {
        PreviewView(context)
    }

    var imageClassifierHelper by remember {
        mutableStateOf<ImageClassifierHelper?>(null)
    }
    var analyzingResult by remember { mutableStateOf<List<Classifications>?>(null) }
    var analyzingInferenceTime by remember { mutableStateOf<Long?>(null) }
    val executor = Executors.newSingleThreadExecutor()
    val preview = remember { androidx.camera.core.Preview.Builder().build() }

    var analyzingResultIsOut: Boolean by remember {
        mutableStateOf(false)
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

    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    var recording by remember {
        mutableStateOf<Recording?>(null)
    }

    val executors = Executors.newSingleThreadExecutor()

    val isRecording by remember {
        mutableStateOf(false)
    }

    var isAnalyzing by remember { mutableStateOf(false) }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraController = remember { LifecycleCameraController(context) }



    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        try {
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (e: Exception) {
            Log.e("RealtimeCameraScreen", "Binding failed", e)
        }
    }

    fun bindAnalysis(cameraProvider: ProcessCameraProvider) {
        try {
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(
                    ResolutionSelector.Builder()
                        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                        .build()
                )
                .setTargetRotation(Surface.ROTATION_0)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()

            imageClassifierHelper = ImageClassifierHelper(
                context = context,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let { result ->
                            if (result.isNotEmpty() && result[0].categories().isNotEmpty()) {
                                analyzingResult = result.sortedByDescending { it.categories()[0].score() }
                                analyzingInferenceTime = inferenceTime
                            } else {
                                analyzingResult = emptyList()
                                analyzingInferenceTime = null
                            }
                            Log.d("TestCameraScreen", result.sortedByDescending { it.categories()[0].score() }.toString())
                        }
                    }
                }
            )

            imageAnalyzer.setAnalyzer(executor) { image ->
                imageClassifierHelper?.classifyImage(image)
                image.close()  // Ensure to close the image to avoid memory leaks
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (e: Exception) {
            Log.e("RealtimeCameraScreen", "Binding failed", e)
        }
    }


    fun takePhoto() {
        cameraController.takePicture(
            executors,
            object : OnImageCapturedCallback() {
                @OptIn(ExperimentalGetImage::class)
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val imageBitmap = image.image?.let {
                        val buffer = it.planes[0].buffer
                        val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }

                    if (imageBitmap == null) {
                        Toast.makeText(context, "Error capturing image", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val imagePath = Helpers.createImageFromBitmap(context, imageBitmap)
                    if (imagePath == null) {
                        Log.e("RealtimeCameraScreen", "Error saving image")
                        return
                    }

                    Log.d("RealtimeCameraScreen", "Image saved to $imagePath")

                    bitmap.value = imageBitmap

                    image.close()

                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(context, "Error capturing image", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    fun recordVideo() {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val outputFile =
            File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "video_$timeStamp.mp4")

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
                        Log.d("RealtimeCameraScreen", "Video saved to ${outputFile.absolutePath}")
                    }
                }
            }
        }
    }

    fun testMediaPipe() {
        cameraController.takePicture(
            executors,
            object : OnImageCapturedCallback() {
                @OptIn(ExperimentalGetImage::class)
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val imageClassifier = ImageClassifierHelper(
                        context = context,
                        classifierListener = object : ImageClassifierHelper.ClassifierListener {
                            override fun onResults(
                                results: List<Classifications>?,
                                inferenceTime: Long
                            ) {
                                results?.let { it ->
                                    if (it.isNotEmpty() && it[0].categories().isEmpty()) {
                                        println(it)
                                        val sortedCategories =
                                            it[0].categories().sortedByDescending { it?.score() }
                                        val displayResult =
                                            sortedCategories.joinToString("\n") {
                                                "${it.categoryName()} " + NumberFormat.getPercentInstance()
                                                    .format(it.score()).trim()
                                            }
                                        Log.d("RealtimeCameraScreen", displayResult)
                                        analyzingResult = it
                                        analyzingResultIsOut = true
                                    } else {
                                        analyzingResult = it
                                        analyzingResultIsOut = true
                                    }
                                }
                            }

                            override fun onError(error: String) {
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    imageClassifier.classifyImage(image)

                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(context, "Error capturing image", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    LaunchedEffect(isAnalyzing, isRecording) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            if (isAnalyzing) {
                bindAnalysis(cameraProvider)
            } else {
                bindPreview(cameraProvider)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Log.d("RealtimeCameraScreen", analyzingResult?.joinToString("\n") { classification ->
        classification.categories().joinToString("\n") { category ->
            "${category.categoryName()} " + NumberFormat.getPercentInstance().format(category.score()).trim()
        }
    }.toString())


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

        if (isAnalyzing) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
                    .background(
                        color = Color1,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = Color2,
                        shape = MaterialTheme.shapes.medium
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = analyzingResult?.joinToString("\n") { classification ->
                                classification.categories().joinToString("\n") { category ->
                                    "${category.categoryName()} " + NumberFormat.getPercentInstance().format(category.score()).trim()
                                }
                            } ?: "No results",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

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
                        )
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(10.dp),
                        onClick = {
//                        if (recording != null) {
//                            recording?.stop()
//                        }
                            isAnalyzing = false
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
                }
            }

        } else {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
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
                    )
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(10.dp),
                    onClick = {
////                        isRecording = true
////                        recordVideo()
//                        testMediaPipe()
                        isAnalyzing = true
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
            }
        }
    }

}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun RealtimeCameraScreenPreview() {
    RealtimeCameraScreen(
        context = LocalContext.current,
        lifecycleOwner = LocalLifecycleOwner.current
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RealtimeCameraScreenDarkPreview() {
    RealtimeCameraScreen(
        context = LocalContext.current,
        lifecycleOwner = LocalLifecycleOwner.current
    )
}