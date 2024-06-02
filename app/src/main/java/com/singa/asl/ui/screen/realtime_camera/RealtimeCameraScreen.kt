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
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.singa.asl.ml.AslTest
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color2
import com.singa.asl.utils.CombinedLandmarkerHelper
import com.singa.asl.utils.Helpers
import com.singa.core.domain.model.FaceLandmarker
import com.singa.core.domain.model.HandLandmarker
import com.singa.core.domain.model.PoseLandmarker
import com.singa.core.utils.DataMapper
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
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
    var combinedLandmarkerHelper by remember { mutableStateOf<CombinedLandmarkerHelper?>(null) }

    // Results from the analyzing
    val analyzingResult by remember { mutableStateOf<List<Classifications>?>(null) }
    var faceLandmarkResult by remember { mutableStateOf<FaceLandmarker?>(null) }
    var handLandmarkResult by remember { mutableStateOf<List<HandLandmarker>?>(null) }
    var poseLandmarkResult by remember { mutableStateOf<List<PoseLandmarker>?>(null) }


    val executor = Executors.newSingleThreadExecutor()
    val preview = remember { androidx.camera.core.Preview.Builder().build() }

    var isFrontCamera by remember {
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


    var cameraSelector by remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }

    fun flattenLandmarks(
        poseResults: List<PoseLandmarker>,
        faceResult: FaceLandmarker?,
        handResults: List<HandLandmarker>?
    ): FloatArray {
        val landmarks = mutableListOf<Float>()

        // Add pose landmarks (33 landmarks, each with x, y, z, visibility)
        for (result in poseResults) {
            for (landmarkList in result.landmarks) {
                for (landmark in landmarkList) {
                    landmarks.add(landmark.x)
                    landmarks.add(landmark.y)
                    landmarks.add(landmark.z)
                    landmarks.add(landmark.visibility.orElse(0.0f)) // Add visibility, default to 0.0f if not present
                }
            }
        }

        // If poseResults are empty, add default values (33 landmarks * 4 floats)
        if (poseResults.isEmpty()) {
            repeat(33) {
                landmarks.add(0.0f)
                landmarks.add(0.0f)
                landmarks.add(0.0f)
                landmarks.add(0.0f) // Add default visibility
            }
        }

        // Add face landmarks (478 landmarks, each with x, y, z)
        if (faceResult != null) {
            for (landmarkList in faceResult.faceLandmarks) {
                for (landmark in landmarkList) {
                    landmarks.add(landmark.x)
                    landmarks.add(landmark.y)
                    landmarks.add(landmark.z)
                }
            }
        } else {
            // Add default values if faceResult is null (478 landmarks * 3 floats)
            repeat(478) {
                landmarks.add(0.0f)
                landmarks.add(0.0f)
                landmarks.add(0.0f)
            }
        }

        // Add hand landmarks (21 landmarks per hand, each with x, y, z)
        if (handResults != null) {
            for (result in handResults) {
                for (landmarkList in result.landmarks) {
                    for (landmark in landmarkList) {
                        landmarks.add(landmark.x)
                        landmarks.add(landmark.y)
                        landmarks.add(landmark.z)
                    }
                }
            }
        }

        // If handResults are empty, add default values (21 landmarks per hand * 3 floats)
        if (handResults.isNullOrEmpty()) {
            repeat(21 * 2) {
                landmarks.add(0.0f)
                landmarks.add(0.0f)
                landmarks.add(0.0f)
            }
        }

        // Pad with zeros if necessary to meet the required size
        val requiredSize = 50760
        while (landmarks.size < requiredSize) {
            landmarks.add(0.0f)
        }

        // Ensure the size does not exceed the required size
        if (landmarks.size > requiredSize) {
            landmarks.subList(0, requiredSize)
        }

        return landmarks.toFloatArray()
    }


    fun createTensorBuffer(landmarks: FloatArray): TensorBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(landmarks.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        for (value in landmarks) {
            byteBuffer.putFloat(value)
        }

        val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, 50760), DataType.FLOAT32)
        inputFeature.loadBuffer(byteBuffer)
        return inputFeature
    }

    fun getPredictedLabel(outputArray: FloatArray): String {
        // Define the list of labels
        val labels = listOf("hello", "thanks", "i-love-you", "see-you-later", "I", "Father")

        // Find the index of the highest probability
        val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1

        // Return the corresponding label
        return if (maxIndex != -1) labels[maxIndex] else "Unknown"
    }

    fun runAslModel(
        context: Context,
        poseResults: List<PoseLandmarker>,
        faceResult: FaceLandmarker?,
        handResults: List<HandLandmarker>?
    ): String {
        val landmarks = flattenLandmarks(poseResults, faceResult, handResults)
        val inputFeature0 = createTensorBuffer(landmarks)

        val model = AslTest.newInstance(context)
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Convert the output to a label
        val outputArray = outputFeature0.floatArray
        Log.d("RealtimeCameraScreen", "Output array: ${outputArray.joinToString()}")
        val predictedLabel = getPredictedLabel(outputArray)

        model.close()
        return predictedLabel
    }


    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        try {
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
            // Common ImageAnalysis configuration function
            fun createImageAnalysis(): ImageAnalysis {
                return ImageAnalysis.Builder()
                    .setResolutionSelector(
                        ResolutionSelector.Builder()
                            .build()
                    )
                    .setTargetRotation(Surface.ROTATION_0)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()
            }

            val imageAnalyzer = createImageAnalysis()

            combinedLandmarkerHelper = CombinedLandmarkerHelper(
                context = context,
                faceLandmarkerHelperListener = object :
                    CombinedLandmarkerHelper.CombinedFaceLandmarkerListener {
                    override fun onResultsFaceLandmarker(result: CombinedLandmarkerHelper.ResultFaceLandmarkerBundle) {
                        val mappedData = DataMapper.mapFaceLandmarkResponseToModel(result.results)
                        faceLandmarkResult = mappedData
                    }


                    override fun onError(error: String, errorCode: Int) {
                        Log.e("RealtimeCameraScreen", "Error: $error, code: $errorCode")
                    }
                },
                handLandmarkerHelperListener = object :
                    CombinedLandmarkerHelper.CombinedHandLandmarkerListener {
                    override fun onResultsHandLandmarker(result: CombinedLandmarkerHelper.ResultHandLandmarkerBundle) {
                        val mappedData = DataMapper.mapHandLandmarkResponseToModel(result.results)
                        handLandmarkResult = mappedData
                    }

                    override fun onError(error: String, errorCode: Int) {
                        Log.e("RealtimeCameraScreen", "Error: $error, code: $errorCode")
                    }
                },
                poseLandmarkerHelperListener = object :
                    CombinedLandmarkerHelper.CombinedPoseLandmarkerListener {
                    override fun onResultsPoseLandmarker(result: CombinedLandmarkerHelper.ResultPoseLandmarkerBundle) {
                        val mappedData = DataMapper.mapPoseLandmarkResponseToModel(result.results)
                        poseLandmarkResult = mappedData
                    }

                    override fun onError(error: String, errorCode: Int) {
                        Log.e("RealtimeCameraScreen", "Error: $error, code: $errorCode")
                    }
                }
            )

            // Set analyzers with image closure handling
            imageAnalyzer.setAnalyzer(executor) { image ->
                image.use {
                    combinedLandmarkerHelper?.detectLiveStream(it, isFrontCamera)
                }
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

    LaunchedEffect(isAnalyzing, isRecording, isFrontCamera) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            if (isAnalyzing) {
                bindAnalysis(cameraProvider)
            } else {
                bindPreview(cameraProvider)
            }
        }, ContextCompat.getMainExecutor(context))

    }

    LaunchedEffect(isFrontCamera) {
        cameraSelector = if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    LaunchedEffect(faceLandmarkResult, handLandmarkResult, poseLandmarkResult) {
        if (faceLandmarkResult != null && handLandmarkResult != null && poseLandmarkResult != null) {
            val result = runAslModel(context, poseLandmarkResult!!, faceLandmarkResult!!, handLandmarkResult!!)

            // Display the result
            Log.d("RealtimeCameraScreen", "ASL Result: $result")
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

        if (isAnalyzing) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
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
                                    "${category.categoryName()} " + NumberFormat.getPercentInstance()
                                        .format(category.score()).trim()
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
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
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
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
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