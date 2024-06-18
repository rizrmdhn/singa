package com.singa.asl.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class CombinedLandmarkHelper(
    private var minPoseDetectionConfidence: Float =DEFAULT_POSE_DETECTION_CONFIDENCE,
    private var minPoseTrackingConfidence: Float = DEFAULT_POSE_TRACKING_CONFIDENCE,
    private var minPosePresenceConfidence: Float = DEFAULT_POSE_PRESENCE_CONFIDENCE,
    private var minHandDetectionConfidence: Float = DEFAULT_HAND_DETECTION_CONFIDENCE,
    private var minHandTrackingConfidence: Float = DEFAULT_HAND_TRACKING_CONFIDENCE,
    private var minHandPresenceConfidence: Float = DEFAULT_HAND_PRESENCE_CONFIDENCE,
    private var minFaceDetectionConfidence: Float = DEFAULT_FACE_DETECTION_CONFIDENCE,
    private var minFaceTrackingConfidence: Float = DEFAULT_FACE_TRACKING_CONFIDENCE,
    private var minFacePresenceConfidence: Float = DEFAULT_FACE_PRESENCE_CONFIDENCE,
    private var maxNumFaces: Int = DEFAULT_NUM_FACES,
    private var maxNumHands: Int = DEFAULT_NUM_HANDS,
    currentModelPose: String = MP_POSE_LANDMARK_TASK,
    currentModelHand: String = MP_HAND_LANDMARK_TASK,
    currentModelFace: String = MP_FACE_LANDMARK_TASK,
    currentDelegate: Int = DELEGATE_CPU,
    private var runningMode: RunningMode = RunningMode.LIVE_STREAM,
    val context: Context,
    // this listener is only used when running in RunningMode.LIVE_STREAM
    val poseLandmarkHelperListener: CombinedPoseLandmarkListener? = null,
    val handLandmarkHelperListener: CombinedHandLandmarkListener? = null,
    val faceLandmarkHelperListener: CombinedFaceLandmarkListener? = null
) {

    private var poseLandmark: PoseLandmarker? = null
    private var handLandmark: HandLandmarker? = null
    private var faceLandmark: FaceLandmarker? = null

    init {
        setupLandmark(
            currentDelegate,
            currentModelFace,
            currentModelPose,
            currentModelHand,
            runningMode,
            faceLandmarkHelperListener,
            poseLandmarkHelperListener,
            handLandmarkHelperListener
        )
    }

    private fun setupLandmark(
        currentDelegate: Int,
        currentModelFace: String,
        currentModelPose: String,
        currentModelHand: String,
        runningMode: RunningMode,
        faceLandmarkHelperListener: CombinedFaceLandmarkListener?,
        poseLandmarkHelperListener: CombinedPoseLandmarkListener?,
        handLandmarkHelperListener: CombinedHandLandmarkListener?
    ) {
        // Setup face landmark
        setupFaceLandmark(
            currentDelegate,
            currentModelFace,
            runningMode,
            faceLandmarkHelperListener
        )

        // Setup pose landmark
        setupPoseLandmark(
            currentDelegate,
            currentModelPose,
            runningMode,
            poseLandmarkHelperListener
        )

        // Setup hand landmark
        setupHandLandmark(
            currentDelegate,
            currentModelHand,
            runningMode,
            handLandmarkHelperListener
        )
    }

    private fun setupFaceLandmark(
        currentDelegate: Int,
        currentModelPath: String,
        runningMode: RunningMode,
        listener: CombinedFaceLandmarkListener?
    ) {
        val baseOptionBuilder = BaseOptions.builder()

        when (currentDelegate) {
            DELEGATE_CPU -> baseOptionBuilder.setDelegate(Delegate.CPU)
            DELEGATE_GPU -> baseOptionBuilder.setDelegate(Delegate.GPU)
        }

        baseOptionBuilder.setModelAssetPath(currentModelPath)

        if (runningMode == RunningMode.LIVE_STREAM) {
            listener
                ?: throw IllegalStateException("faceLandmarkHelperListener must be set when runningMode is LIVE_STREAM.")
        }

        try {
            val baseOptions = baseOptionBuilder.build()
            val optionsBuilder = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinFaceDetectionConfidence(minFaceDetectionConfidence)
                .setMinTrackingConfidence(minFaceTrackingConfidence)
                .setMinFacePresenceConfidence(minFacePresenceConfidence)
                .setNumFaces(maxNumFaces)
                .setRunningMode(runningMode)

            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder
                    .setResultListener(this::returnLivestreamFaceLandmarkResult)
                    .setErrorListener(this::returnLivestreamFaceLandmarkError)
            }

            val options = optionsBuilder.build()
            faceLandmark = FaceLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            listener?.onError("Face Landmark failed to initialize. See error logs for details")
            Log.e(
                TAG,
                "MediaPipe failed to load the task with error: ${e.message}"
            )
        } catch (e: RuntimeException) {
            listener?.onError(
                "Face Landmark failed to initialize. See error logs for details",
                GPU_ERROR
            )
            Log.e(
               TAG,
                "Face Landmark failed to load model with error: ${e.message}"
            )
        }
    }

    private fun setupPoseLandmark(
        currentDelegate: Int,
        currentModelPath: String,
        runningMode: RunningMode,
        listener: CombinedPoseLandmarkListener?
    ) {
        val baseOptionBuilder = BaseOptions.builder()

        when (currentDelegate) {
            DELEGATE_CPU -> baseOptionBuilder.setDelegate(Delegate.CPU)
            DELEGATE_GPU -> baseOptionBuilder.setDelegate(Delegate.GPU)
        }

        baseOptionBuilder.setModelAssetPath(currentModelPath)

        if (runningMode == RunningMode.LIVE_STREAM) {
            listener
                ?: throw IllegalStateException("poseLandmarkHelperListener must be set when runningMode is LIVE_STREAM.")
        }

        try {
            val baseOptions = baseOptionBuilder.build()
            val optionsBuilder = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinPoseDetectionConfidence(minPoseDetectionConfidence)
                .setMinTrackingConfidence(minPoseTrackingConfidence)
                .setMinPosePresenceConfidence(minPosePresenceConfidence)
                .setRunningMode(runningMode)

            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder
                    .setResultListener(this::returnLivestreamPoseLandmarkResult)
                    .setErrorListener(this::returnLivestreamPoseLandmarkError)
            }

            val options = optionsBuilder.build()
            poseLandmark = PoseLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            listener?.onError("Pose Landmark failed to initialize. See error logs for details")
            Log.e(
                TAG,
                "MediaPipe failed to load the task with error: ${e.message}"
            )
        } catch (e: RuntimeException) {
            listener?.onError(
                "Pose Landmark failed to initialize. See error logs for details",
                GPU_ERROR
            )
            Log.e(
                TAG,
                "Pose Landmark failed to load model with error: ${e.message}"
            )
        }
    }

    private fun setupHandLandmark(
        currentDelegate: Int,
        currentModelPath: String,
        runningMode: RunningMode,
        listener: CombinedHandLandmarkListener?
    ) {
        val baseOptionBuilder = BaseOptions.builder()

        when (currentDelegate) {
            DELEGATE_CPU -> baseOptionBuilder.setDelegate(Delegate.CPU)
            DELEGATE_GPU -> baseOptionBuilder.setDelegate(Delegate.GPU)
        }

        baseOptionBuilder.setModelAssetPath(currentModelPath)

        if (runningMode == RunningMode.LIVE_STREAM) {
            listener
                ?: throw IllegalStateException("handLandmarkHelperListener must be set when runningMode is LIVE_STREAM.")
        }

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
                    .setResultListener(this::returnLivestreamHandLandMarkerResult)
                    .setErrorListener(this::returnLivestreamHandLandMarkerError)
            }

            val options = optionsBuilder.build()
            handLandmark = HandLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            listener?.onError("Hand Landmark failed to initialize. See error logs for details")
            Log.e(
                TAG,
                "MediaPipe failed to load the task with error: ${e.message}"
            )
        } catch (e: RuntimeException) {
            listener?.onError(
                "Hand Landmark failed to initialize. See error logs for details",
                GPU_ERROR
            )
            Log.e(
                TAG,
                "Hand Landmark failed to load model with error: ${e.message}"
            )
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
            // Rotate the image according to its rotation degrees
            postRotate(
                imageProxy.imageInfo.rotationDegrees.toFloat(),
                imageProxy.width / 2f,
                imageProxy.height / 2f
            )
            // Conditionally mirror the image if it's from the front camera
            if (isFrontCamera) {
                postScale(-1f, 1f, imageProxy.width / 2f, imageProxy.height / 2f)
            }
        }


        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
            matrix, true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        handLandmark?.detectAsync(mpImage, frameTime)
        faceLandmark?.detectAsync(mpImage, frameTime)
        poseLandmark?.detectAsync(mpImage, frameTime)
    }


    private fun returnLivestreamHandLandMarkerResult(
        result: HandLandmarkerResult,
        input: MPImage,
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        handLandmarkHelperListener?.onResultsHandLandmark(
            ResultHandLandmarkBundle(
                listOf(result),
                inferenceTime,
                input.height,
                input.width
            )
        )
    }

    private fun returnLivestreamHandLandMarkerError(error: RuntimeException) {
        handLandmarkHelperListener?.onError(error.message ?: "An unknown error has occurred")
    }

    private fun returnLivestreamPoseLandmarkResult(
        result: PoseLandmarkerResult,
        input: MPImage
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        poseLandmarkHelperListener?.onResultsPoseLandmark(
            ResultPoseLandmarkBundle(
                listOf(result),
                inferenceTime,
                input.height,
                input.width
            )
        )
    }

    private fun returnLivestreamPoseLandmarkError(error: RuntimeException) {
        poseLandmarkHelperListener?.onError(
            error.message ?: "An unknown error has occurred"
        )
    }

    private fun returnLivestreamFaceLandmarkResult(
        result: FaceLandmarkerResult,
        input: MPImage
    ) {
        if (result.faceLandmarks().size > 0) {
            val finishTimeMs = SystemClock.uptimeMillis()
            val inferenceTime = finishTimeMs - result.timestampMs()

            faceLandmarkHelperListener?.onResultsFaceLandmark(
                ResultFaceLandmarkBundle(
                    result,
                    inferenceTime,
                    input.height,
                    input.width
                )
            )
        } else {
            faceLandmarkHelperListener?.onError("No face detected")
        }
    }

    private fun returnLivestreamFaceLandmarkError(error: RuntimeException) {
        faceLandmarkHelperListener?.onError(
            error.message ?: "An unknown error has occurred"
        )
    }

    companion object {
        const val TAG = "CombinedLandmarkHelper"
        private const val MP_FACE_LANDMARK_TASK = "face_landmark.task"
        private const val MP_HAND_LANDMARK_TASK = "hand_landmark.task"
        private const val MP_POSE_LANDMARK_TASK = "pose_landmark.task"

        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_FACE_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_FACE_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_FACE_PRESENCE_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.5F
        const val DEFAULT_POSE_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_POSE_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_POSE_PRESENCE_CONFIDENCE = 0.5F
        const val DEFAULT_NUM_FACES = 1
        const val DEFAULT_NUM_HANDS = 2
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
    }

    data class ResultFaceLandmarkBundle(
        val results: FaceLandmarkerResult,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    data class ResultHandLandmarkBundle(
        val results: List<HandLandmarkerResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    data class ResultPoseLandmarkBundle(
        val results: List<PoseLandmarkerResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    interface CombinedFaceLandmarkListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResultsFaceLandmark(result: ResultFaceLandmarkBundle)
    }

    interface CombinedHandLandmarkListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResultsHandLandmark(result: ResultHandLandmarkBundle)
    }

    interface CombinedPoseLandmarkListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResultsPoseLandmark(result: ResultPoseLandmarkBundle)
    }
}