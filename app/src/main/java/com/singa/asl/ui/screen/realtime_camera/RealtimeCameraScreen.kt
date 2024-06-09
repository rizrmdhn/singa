package com.singa.asl.ui.screen.realtime_camera

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recording
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.singa.asl.R
import com.singa.asl.ml.AslTest
import com.singa.asl.ui.components.LandmarkOverlay
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color2
import com.singa.asl.utils.CombinedLandmarkerHelper
import com.singa.core.domain.model.FaceLandmarker
import com.singa.core.domain.model.HandLandmarker
import com.singa.core.domain.model.PoseLandmarker
import com.singa.core.utils.DataMapper
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
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


    var aslResults by remember { mutableStateOf<String?>(null) }
    var faceLandmarkResult by remember { mutableStateOf<FaceLandmarker?>(null) }
    var handLandmarkResult by remember { mutableStateOf<List<HandLandmarker>?>(null) }
    var poseLandmarkResult by remember { mutableStateOf<List<PoseLandmarker>?>(null) }

    val sequences by remember { mutableStateOf<MutableList<List<Float>>>(mutableListOf()) }

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

    fun prepareLandmarks(
        poseResults: List<PoseLandmarker>,
        faceResult: FaceLandmarker?,
        handResults: List<HandLandmarker>?
    ): List<Float> {
        val landmarks = mutableListOf<Float>()

        // Pose landmarks: 33 landmarks each with 4 values (x, y, z, visibility)
        val poseLandmarks = mutableListOf<Float>()
        for (result in poseResults) {
            for (landmarkList in result.landmarks) {
                for (landmark in landmarkList) {
                    poseLandmarks.add(landmark.x)
                    poseLandmarks.add(landmark.y)
                    poseLandmarks.add(landmark.z)
                    poseLandmarks.add(landmark.visibility.orElse(0.0f))
                }
            }
        }
        // Pad pose landmarks to 33 * 4 = 132 values
        while (poseLandmarks.size < 132) {
            poseLandmarks.add(0.0f)
        }

        // Face landmarks: 478 landmarks each with 3 values (x, y, z)
        val faceLandmarks = mutableListOf<Float>()
        if (faceResult != null) {
            for (landmarkList in faceResult.faceLandmarks) {
                for (landmark in landmarkList) {
                    faceLandmarks.add(landmark.x)
                    faceLandmarks.add(landmark.y)
                    faceLandmarks.add(landmark.z)
                }
            }
        }
        // Pad face landmarks to 478 * 3 = 1434 values
        while (faceLandmarks.size < 1434) {
            faceLandmarks.add(0.0f)
        }

        // Hand landmarks: 21 landmarks each with 3 values (x, y, z) for each hand
        val leftHandLandmarks = mutableListOf<Float>()
        val rightHandLandmarks = mutableListOf<Float>()
        if (handResults != null) {
            for (result in handResults) {
                for (landmarkList in result.landmarks) {
                    for (landmark in landmarkList) {
                        if (result.landmarks.indexOf(landmarkList) % 2 == 0) {
                            leftHandLandmarks.add(landmark.x)
                            leftHandLandmarks.add(landmark.y)
                            leftHandLandmarks.add(landmark.z)
                        } else {
                            rightHandLandmarks.add(landmark.x)
                            rightHandLandmarks.add(landmark.y)
                            rightHandLandmarks.add(landmark.z)
                        }
                    }
                }
            }
        }
        // Pad left and right hand landmarks to 21 * 3 = 63 values each
        while (leftHandLandmarks.size < 63) {
            leftHandLandmarks.add(0.0f)
        }
        while (rightHandLandmarks.size < 63) {
            rightHandLandmarks.add(0.0f)
        }
        landmarks.addAll(faceLandmarks)
        landmarks.addAll(poseLandmarks)
        landmarks.addAll(rightHandLandmarks)
        landmarks.addAll(leftHandLandmarks)

        return landmarks.toList()
    }




    fun convertListToByteBuffer(input: List<List<Float>>): ByteBuffer {
        val flatList = input.flatten()
        val byteBuffer = ByteBuffer.allocateDirect(flatList.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        for (value in flatList) {
            byteBuffer.putFloat(value)
        }

        val inputFeature = TensorBuffer.createFixedSize(
            intArrayOf(1, 30, 1692),
            DataType.FLOAT32
        )
        inputFeature.loadBuffer(byteBuffer)

        return byteBuffer
    }


    fun getPredictedLabel(outputArray: FloatArray): String {
        val labels = listOf("hello", "thanks", "i-love-you", "see-you-later", "I", "Father")

        val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1

        // remove - in the label and capitalize the first letter
        return if (maxIndex >= 0) labels[maxIndex].replace("-", " ")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } else "Unknown"
    }


    fun runAslModel(
        context: Context,
        poseResults: List<PoseLandmarker>,
        faceResult: FaceLandmarker?,
        handResults: List<HandLandmarker>?
    ): String {
        val sequenceLength = 30
        val threshold = 0.5f
        val maxSequences = 90

        val landmarks = prepareLandmarks(poseResults, faceResult, handResults)

        sequences.add(landmarks)
        val snapshot = sequences.takeLast(30)

        val model = AslTest.newInstance(context)

        var predictedLabel = ""
        val predictions = mutableListOf<Int>()

        // collect keypoints until sequence is full
        if (snapshot.size == sequenceLength) {
            val byteBuffer = convertListToByteBuffer(snapshot)

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 30, 1692), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val outputArray = outputFeature0.floatArray

            val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1

            predictions.add(maxIndex)
            val last10Predictions = predictions.takeLast(10)

            // get the most common element in the last 10 predictions
            val mostCommonPrediction = last10Predictions.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key

            // check if the most common prediction matches the index of the maximum value in the result array
            val matches = mostCommonPrediction == maxIndex

            if (matches) {
                if (outputArray[maxIndex] > threshold) {
                    Log.d("RealtimeCameraScreen", "Predicted label: ${getPredictedLabel(outputArray)}")
                    predictedLabel = getPredictedLabel(outputArray)
                }
            }
        }

        model.close()

        // make sure sequences list does not exceed maximum length
        // since we will only use the last 30 frame, then we can
        // remove the older frame
        if (sequences.size > maxSequences) {
            sequences.removeAt(0)
        }

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
            val result = runAslModel(
                context,
                poseLandmarkResult!!,
                faceLandmarkResult!!,
                handLandmarkResult!!
            )

            // Display the result
            aslResults = result
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
           LandmarkOverlay(
               poseLandmarks = poseLandmarkResult,
               faceLandmarks = faceLandmarkResult,
               handLandmarks = handLandmarkResult,
           )
       }

        if (isAnalyzing) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val aslResultArray = remember {
                    mutableListOf("")
                }

                val previousResult = remember {
                    mutableStateOf("")
                }

                previousResult.value = aslResults ?: ""

                if(previousResult.value != aslResultArray[0] && previousResult.value != ""){
                    aslResultArray.add(0,previousResult.value)
                }

                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(0.2f)
                        .background(
                            color = Color(0x872F2E41),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        // Display the result to combine word to paragraph

                        Text(
                            text = aslResultArray.joinToString(" "),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
//                        Text(
//                            text = analyzingResult?.joinToString("\n") { classification ->
//                                classification.categories().joinToString("\n") { category ->
//                                    "${category.categoryName()} " + NumberFormat.getPercentInstance()
//                                        .format(category.score()).trim()
//                                }
//                            } ?: "No results",
//                            color = Color.White,
//                            style = MaterialTheme.typography.bodySmall
//                        )
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
                            aslResultArray.clear()
                            previousResult.value = ""
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