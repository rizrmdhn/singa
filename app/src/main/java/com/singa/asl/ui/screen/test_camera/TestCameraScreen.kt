package com.singa.asl.ui.screen.test_camera

import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.components.containers.Classifications
import com.singa.asl.utils.ImageClassifierHelper
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.concurrent.Executors

@Composable
fun TestCameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageClassifierHelper by remember {
        mutableStateOf<ImageClassifierHelper?>(null)
    }

    var analyzingResult by remember { mutableStateOf<List<Classifications>?>(null) }
    var analyzingInferenceTime by remember { mutableStateOf<Long?>(null) }
    val executor = Executors.newSingleThreadExecutor()

    val preview = remember { Preview.Builder().build() }

    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        val listener = Runnable {
            val cameraProvider = cameraProviderFuture.get()

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
                            Log.d("TestCameraScreen", result.toString())
                        }
                    }
                }
            )

            imageAnalyzer.setAnalyzer(executor) { image ->
                imageClassifierHelper?.classifyImage(image)
                image.close()  // Ensure to close the image to avoid memory leaks
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Toast.makeText(context, "Failed to start camera.", Toast.LENGTH_SHORT).show()
            }
        }

        cameraProviderFuture.addListener(listener, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraProviderFuture.get().unbindAll()
            executor.shutdown()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // Adjust the delay duration here (5000ms for 5 seconds)
            imageClassifierHelper?.let { _ ->
                // No need to call any function as the imageAnalyzer continuously feeds images
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PreviewView(context).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    preview.setSurfaceProvider(surfaceProvider)
                }
            }
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .background(Color(0xAA000000), RoundedCornerShape(8.dp))
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
            Text(
                text = analyzingInferenceTime?.let { "$it ms" } ?: "",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}






