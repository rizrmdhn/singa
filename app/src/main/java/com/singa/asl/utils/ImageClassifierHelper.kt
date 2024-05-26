package com.singa.asl.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier
import com.google.mediapipe.tasks.components.containers.Classifications
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.singa.asl.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.ByteArrayOutputStream

class ImageClassifierHelper(
    private var threshold: Float = 0.1f,
    private var maxResults: Int = 3,
    private val modelName: String = "mobilenet_v1.tflite",
    private val runningMode: RunningMode = RunningMode.LIVE_STREAM,
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
            .setRunningMode(runningMode)

        if (runningMode == RunningMode.LIVE_STREAM) {
            optionsBuilder.setResultListener { result, image ->
                val finishTimeMs = SystemClock.uptimeMillis()
                val inferenceTime = finishTimeMs - result.timestampMs()
                classifierListener?.onResults(
                    result.classificationResult().classifications(),
                    inferenceTime
                )
            }.setErrorListener { error ->
                classifierListener?.onError(error.message.toString())
            }
        }

        val baseOptionsBuilder = BaseOptions.builder()
            .setModelAssetPath(modelName)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromOptions(
                context,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    fun classifyImage(image: ImageProxy) {

        if (imageClassifier == null) {
            setupImageClassifier()
        }

        val bitmapImage = image.toBitmap()

        Log.d(TAG, "Classifying image...")

        val mpImage = BitmapImageBuilder(bitmapImage).build()

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.UINT8))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmapImage))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setRotationDegrees(image.imageInfo.rotationDegrees)
            .build()

        val inferenceTime = SystemClock.uptimeMillis()
        imageClassifier?.classifyAsync(mpImage, imageProcessingOptions, inferenceTime)
    }

    private fun toBitmap(image: ImageProxy): Bitmap? {
        // Ensure the image format is YUV_420_888
        if (image.format != ImageFormat.YUV_420_888) {
            throw IllegalArgumentException("Unsupported image format: ${image.format}")
        }

        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        // Copy the Y buffer
        yBuffer.get(nv21, 0, ySize)

        // Interleave U and V buffers
        var uvIndex = ySize
        val pixelStride = image.planes[1].pixelStride
        val rowStride = image.planes[1].rowStride
        val chromaHeight = image.height / 2
        val chromaWidth = image.width / 2

        for (row in 0 until chromaHeight) {
            for (col in 0 until chromaWidth) {
                val uIndex = row * rowStride + col * pixelStride
                nv21[uvIndex++] = uBuffer[uIndex]
                nv21[uvIndex++] = vBuffer[uIndex]
            }
        }

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val jpegArray = out.toByteArray()
        return BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.size)
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}