package com.singa.asl.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandLandmarkerHelper(
    private var minHandDetectionConfidence: Float = DEFAULT_HAND_DETECTION_CONFIDENCE,
    private var minHandTrackingConfidence: Float = DEFAULT_HAND_TRACKING_CONFIDENCE,
    private var minHandPresenceConfidence: Float = DEFAULT_HAND_PRESENCE_CONFIDENCE,
    private var maxNumHands: Int = DEFAULT_NUM_HANDS,
    private var currentDelegate: Int = DELEGATE_CPU,
    private var runningMode: RunningMode = RunningMode.LIVE_STREAM,
    val context: Context,
    val handLandmarkerHelperListener: LandmarkerListener? = null
) {
    private var handLandmarker: HandLandmarker? = null

    init {
        setupHandLandmarker()
    }

    fun clearHandLandmarker() {
        handLandmarker?.close()
        handLandmarker = null
    }

    private fun setupHandLandmarker() {
        val baseOptionBuilder = BaseOptions.builder()
        when (currentDelegate) {
            DELEGATE_CPU -> baseOptionBuilder.setDelegate(Delegate.CPU)
            DELEGATE_GPU -> baseOptionBuilder.setDelegate(Delegate.GPU)
        }
        baseOptionBuilder.setModelAssetPath(MP_HAND_LANDMARKER_TASK)
        try {
            val baseOptions = baseOptionBuilder.build()
            val optionsBuilder = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinHandDetectionConfidence(minHandDetectionConfidence)
                .setMinTrackingConfidence(minHandTrackingConfidence)
                .setMinHandPresenceConfidence(minHandPresenceConfidence)
                .setNumHands(maxNumHands)
                .setRunningMode(runningMode)

            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder
                    .setResultListener(this::returnLivestreamResult)
                    .setErrorListener(this::returnLivestreamError)
            }

            val options = optionsBuilder.build()
            handLandmarker = HandLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            handLandmarkerHelperListener?.onError(
                "Hand Landmarker failed to initialize. See error logs for details"
            )
            Log.e(TAG, "MediaPipe failed to load the task with error: ${e.message}")
        } catch (e: RuntimeException) {
            handLandmarkerHelperListener?.onError(
                "Hand Landmarker failed to initialize. See error logs for details"
            )
            Log.e(TAG, "MediaPipe failed to load the task with error: ${e.message}")
        }
    }

    fun detectLiveStream(
        imageProxy: ImageProxy,
        isFrontCamera: Boolean,
    ) {
        if (runningMode != RunningMode.LIVE_STREAM) {
            throw IllegalArgumentException(
                "Attempting to call detectLiveStream while not using RunningMode.LIVE_STREAM"
            )
        }

        val frameTime = SystemClock.uptimeMillis()

        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width,
            imageProxy.height,
            Bitmap.Config.ARGB_8888
        )

        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            if (isFrontCamera) {
                postScale(-1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat())
            }
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
            matrix, true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        detectAsync(mpImage, frameTime)
    }

    private fun detectAsync(mpImage: MPImage, frameTime: Long) {
        handLandmarker?.detectAsync(mpImage, frameTime) ?: Log.e(TAG, "HandLandmarker is null")
    }

    fun detectVideoFile(
        videoUri: Uri,
        inferenceIntervalMs: Long
    ): ResultBundle? {
        if (runningMode != RunningMode.VIDEO) {
            throw IllegalArgumentException(
                "Attempting to call detectVideoFile while not using RunningMode.VIDEO"
            )
        }

        val startTime = SystemClock.uptimeMillis()

        var didErrorOccurred = false

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val videoLengthMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
        val firstFrame = retriever.getFrameAtTime(0)
        val width = firstFrame?.width
        val height = firstFrame?.height

        if (videoLengthMs == null || width == null || height == null) return null

        val resultList = mutableListOf<HandLandmarkerResult>()
        val numberOfFrameToRead = videoLengthMs.div(inferenceIntervalMs)

        for (i in 0..numberOfFrameToRead) {
            val timestampMs = i * inferenceIntervalMs // ms

            retriever.getFrameAtTime(timestampMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST)?.let { frame ->
                val argb8888Frame = if (frame.config == Bitmap.Config.ARGB_8888) frame
                else frame.copy(Bitmap.Config.ARGB_8888, false)

                val mpImage = BitmapImageBuilder(argb8888Frame).build()

                handLandmarker?.detectForVideo(mpImage, timestampMs)?.let { detectionResult ->
                    resultList.add(detectionResult)
                } ?: run {
                    didErrorOccurred = true
                    handLandmarkerHelperListener?.onError("ResultBundle could not be returned in detectVideoFile")
                }
            } ?: run {
                didErrorOccurred = true
                handLandmarkerHelperListener?.onError("Frame at specified time could not be retrieved when detecting in video.")
            }
        }

        retriever.release()

        val inferenceTimePerFrameMs = (SystemClock.uptimeMillis() - startTime).div(numberOfFrameToRead)

        return if (didErrorOccurred) {
            null
        } else {
            ResultBundle(resultList, inferenceTimePerFrameMs, height, width)
        }
    }

    fun detectImage(image: Bitmap): ResultBundle? {
        if (runningMode != RunningMode.IMAGE) {
            throw IllegalArgumentException(
                "Attempting to call detectImage while not using RunningMode.IMAGE"
            )
        }

        val startTime = SystemClock.uptimeMillis()

        val mpImage = BitmapImageBuilder(image).build()

        handLandmarker?.detect(mpImage)?.let { landmarkResult ->
            val inferenceTime = SystemClock.uptimeMillis() - startTime
            return ResultBundle(
                listOf(landmarkResult),
                inferenceTime,
                image.height,
                image.width
            )
        } ?: run {
            handLandmarkerHelperListener?.onError("Hand Landmarker failed to detect.")
            return null
        }
    }

    private fun returnLivestreamResult(
        result: HandLandmarkerResult,
        input: MPImage
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        handLandmarkerHelperListener?.onResults(
            ResultBundle(
                listOf(result),
                inferenceTime,
                input.height,
                input.width
            )
        )
    }

    private fun returnLivestreamError(error: RuntimeException) {
        handLandmarkerHelperListener?.onError(error.message ?: "An unknown error has occurred")
    }

    companion object {
        const val TAG = "HandLandmarkerHelper"
        const val MP_HAND_LANDMARKER_TASK = "hand_landmarker.task"


        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_HAND_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.5F
        const val DEFAULT_NUM_HANDS = 2
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
    }

    data class ResultBundle(
        val results: List<HandLandmarkerResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    interface LandmarkerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(resultBundle: ResultBundle)
    }
}
