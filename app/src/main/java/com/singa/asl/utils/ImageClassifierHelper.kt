package com.singa.asl.utils

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class ImageClassifierHelper(
    private var threshold: Float = 0.1f,
    private var maxResults: Int = 3,
    private val modelName: String = "asl-test.tflite",
    private val runningMode: RunningMode = RunningMode.VIDEO,
    val context: Context,
    val handLandmarkerListener: HandLandmarkerListener?,
    val faceLandmarkerListener: FaceLandmarkerListener?,
    val poseLandmarkerListener: PoseLandmarkerListener?
) {
    private var faceLandmarker: FaceLandmarker? = null
    private var handLandmarker: HandLandmarker? = null
    private var poseLandmarker: PoseLandmarker? = null

    init {
        setupLandmarkers()
    }

    private fun setupLandmarkers() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(modelName)
            .setDelegate(Delegate.GPU)
            .build()

        setupFaceLandmarker(baseOptions)
        setupHandLandmarker(baseOptions)
        setupPoseLandmarker(baseOptions)
    }

    private fun setupFaceLandmarker(baseOptions: BaseOptions) {
        try {
            val options = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinFaceDetectionConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setNumFaces(1)
                .setRunningMode(runningMode)
                .build()

            faceLandmarker = FaceLandmarker.createFromOptions(context, options)
            Log.d(TAG, "FaceLandmarker successfully created")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating FaceLandmarker: $e")
            faceLandmarkerListener?.onError("Error creating FaceLandmarker: $e")
        }
    }

    private fun setupHandLandmarker(baseOptions: BaseOptions) {
        try {
            val options = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinHandDetectionConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setMinHandPresenceConfidence(0.5f)
                .setNumHands(1)
                .setRunningMode(runningMode)
                .build()

            handLandmarker = HandLandmarker.createFromOptions(context, options)
            Log.d(TAG, "HandLandmarker successfully created")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating HandLandmarker: $e")
            handLandmarkerListener?.onError("Error creating HandLandmarker: $e")
        }
    }

    private fun setupPoseLandmarker(baseOptions: BaseOptions) {
        try {
            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinPoseDetectionConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setNumPoses(1)
                .setRunningMode(runningMode)
                .build()

            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
            Log.d(TAG, "PoseLandmarker successfully created")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating PoseLandmarker: $e")
            poseLandmarkerListener?.onError("Error creating PoseLandmarker: $e")
        }
    }

    fun classifyImage(image: ImageProxy) {
        val bitmapImage = image.toBitmap()
        Log.d(TAG, "Classifying image...")

        val mpImage = BitmapImageBuilder(bitmapImage).build()
        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setRotationDegrees(image.imageInfo.rotationDegrees)
            .build()

        val inferenceTime = SystemClock.uptimeMillis()

        // Hand Landmarker
        handLandmarker?.detectForVideo(mpImage, inferenceTime)?.let {
            handLandmarkerListener?.onResults(it, inferenceTime)
        }

        // Face Landmarker
        faceLandmarker?.detectForVideo(mpImage, inferenceTime)?.let {
            faceLandmarkerListener?.onResults(it, inferenceTime)
        }

        // Pose Landmarker
        poseLandmarker?.detectForVideo(mpImage, inferenceTime)?.let {
            poseLandmarkerListener?.onResults(it, inferenceTime)
        }
    }

    interface HandLandmarkerListener {
        fun onError(error: String)
        fun onResults(results: HandLandmarkerResult?, inferenceTime: Long)
    }

    interface FaceLandmarkerListener {
        fun onError(error: String)
        fun onResults(results: FaceLandmarkerResult?, inferenceTime: Long)
    }

    interface PoseLandmarkerListener {
        fun onError(error: String)
        fun onResults(results: PoseLandmarkerResult?, inferenceTime: Long)
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}