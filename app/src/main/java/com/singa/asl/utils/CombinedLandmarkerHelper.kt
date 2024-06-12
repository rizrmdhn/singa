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

class CombinedLandmarkerHelper(
    private var minPoseDetectionConfidence: Float = PoseLandmarkerHelper.DEFAULT_POSE_DETECTION_CONFIDENCE,
    private var minPoseTrackingConfidence: Float = PoseLandmarkerHelper.DEFAULT_POSE_TRACKING_CONFIDENCE,
    private var minPosePresenceConfidence: Float = PoseLandmarkerHelper.DEFAULT_POSE_PRESENCE_CONFIDENCE,
    private var minHandDetectionConfidence: Float = HandLandmarkerHelper.DEFAULT_HAND_DETECTION_CONFIDENCE,
    private var minHandTrackingConfidence: Float = HandLandmarkerHelper.DEFAULT_HAND_TRACKING_CONFIDENCE,
    private var minHandPresenceConfidence: Float = HandLandmarkerHelper.DEFAULT_HAND_PRESENCE_CONFIDENCE,
    private var minFaceDetectionConfidence: Float = FaceLandmarkerHelper.DEFAULT_FACE_DETECTION_CONFIDENCE,
    private var minFaceTrackingConfidence: Float = FaceLandmarkerHelper.DEFAULT_FACE_TRACKING_CONFIDENCE,
    private var minFacePresenceConfidence: Float = FaceLandmarkerHelper.DEFAULT_FACE_PRESENCE_CONFIDENCE,
    private var maxNumFaces: Int = FaceLandmarkerHelper.DEFAULT_NUM_FACES,
    private var maxNumHands: Int = HandLandmarkerHelper.DEFAULT_NUM_HANDS,
    currentModelPose: String = MP_POSE_LANDMARKER_TASK,
    currentModelHand: String = HandLandmarkerHelper.MP_HAND_LANDMARKER_TASK,
    currentModelFace: String = FaceLandmarkerHelper.MP_FACE_LANDMARKER_TASK,
    currentDelegate: Int = PoseLandmarkerHelper.DELEGATE_CPU,
    private var runningMode: RunningMode = RunningMode.LIVE_STREAM,
    val context: Context,
    // this listener is only used when running in RunningMode.LIVE_STREAM
    val poseLandmarkerHelperListener: CombinedPoseLandmarkerListener? = null,
    val handLandmarkerHelperListener: CombinedHandLandmarkerListener? = null,
    val faceLandmarkerHelperListener: CombinedFaceLandmarkerListener? = null
) {

    private var poseLandmarker: PoseLandmarker? = null
    private var handLandmarker: HandLandmarker? = null
    private var faceLandmarker: FaceLandmarker? = null

    init {
        setupLandmarkers(
            currentDelegate,
            currentModelFace,
            currentModelPose,
            currentModelHand,
            runningMode,
            faceLandmarkerHelperListener,
            poseLandmarkerHelperListener,
            handLandmarkerHelperListener
        )
    }

    fun setupLandmarkers(
        currentDelegate: Int,
        currentModelFace: String,
        currentModelPose: String,
        currentModelHand: String,
        runningMode: RunningMode,
        faceLandmarkerHelperListener: CombinedFaceLandmarkerListener?,
        poseLandmarkerHelperListener: CombinedPoseLandmarkerListener?,
        handLandmarkerHelperListener: CombinedHandLandmarkerListener?
    ) {
        // Setup face landmarker
        setupFaceLandmarker(
            currentDelegate,
            currentModelFace,
            runningMode,
            faceLandmarkerHelperListener
        )

        // Setup pose landmarker
        setupPoseLandmarker(
            currentDelegate,
            currentModelPose,
            runningMode,
            poseLandmarkerHelperListener
        )

        // Setup hand landmarker
        setupHandLandmarker(
            currentDelegate,
            currentModelHand,
            runningMode,
            handLandmarkerHelperListener
        )
    }

    private fun setupFaceLandmarker(
        currentDelegate: Int,
        currentModelPath: String,
        runningMode: RunningMode,
        listener: CombinedFaceLandmarkerListener?
    ) {
        val baseOptionBuilder = BaseOptions.builder()

        when (currentDelegate) {
            DELEGATE_CPU -> baseOptionBuilder.setDelegate(Delegate.CPU)
            DELEGATE_GPU -> baseOptionBuilder.setDelegate(Delegate.GPU)
        }

        baseOptionBuilder.setModelAssetPath(currentModelPath)

        if (runningMode == RunningMode.LIVE_STREAM) {
            listener
                ?: throw IllegalStateException("faceLandmarkerHelperListener must be set when runningMode is LIVE_STREAM.")
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
                    .setResultListener(this::returnLivestreamFaceLandmarkerResult)
                    .setErrorListener(this::returnLivestreamFaceLandmarkerError)
            }

            val options = optionsBuilder.build()
            faceLandmarker = FaceLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            listener?.onError("Face Landmarker failed to initialize. See error logs for details")
            Log.e(
                FaceLandmarkerHelper.TAG,
                "MediaPipe failed to load the task with error: ${e.message}"
            )
        } catch (e: RuntimeException) {
            listener?.onError(
                "Face Landmarker failed to initialize. See error logs for details",
                FaceLandmarkerHelper.GPU_ERROR
            )
            Log.e(
                FaceLandmarkerHelper.TAG,
                "Face Landmarker failed to load model with error: ${e.message}"
            )
        }
    }

    private fun setupPoseLandmarker(
        currentDelegate: Int,
        currentModelPath: String,
        runningMode: RunningMode,
        listener: CombinedPoseLandmarkerListener?
    ) {
        val baseOptionBuilder = BaseOptions.builder()

        when (currentDelegate) {
            DELEGATE_CPU -> baseOptionBuilder.setDelegate(Delegate.CPU)
            DELEGATE_GPU -> baseOptionBuilder.setDelegate(Delegate.GPU)
        }

        baseOptionBuilder.setModelAssetPath(currentModelPath)

        if (runningMode == RunningMode.LIVE_STREAM) {
            listener
                ?: throw IllegalStateException("poseLandmarkerHelperListener must be set when runningMode is LIVE_STREAM.")
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
            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            listener?.onError("Pose Landmarker failed to initialize. See error logs for details")
            Log.e(
                PoseLandmarkerHelper.TAG,
                "MediaPipe failed to load the task with error: ${e.message}"
            )
        } catch (e: RuntimeException) {
            listener?.onError(
                "Pose Landmarker failed to initialize. See error logs for details",
                PoseLandmarkerHelper.GPU_ERROR
            )
            Log.e(
                PoseLandmarkerHelper.TAG,
                "Pose Landmarker failed to load model with error: ${e.message}"
            )
        }
    }

    private fun setupHandLandmarker(
        currentDelegate: Int,
        currentModelPath: String,
        runningMode: RunningMode,
        listener: CombinedHandLandmarkerListener?
    ) {
        val baseOptionBuilder = BaseOptions.builder()

        when (currentDelegate) {
            DELEGATE_CPU -> baseOptionBuilder.setDelegate(Delegate.CPU)
            DELEGATE_GPU -> baseOptionBuilder.setDelegate(Delegate.GPU)
        }

        baseOptionBuilder.setModelAssetPath(currentModelPath)

        if (runningMode == RunningMode.LIVE_STREAM) {
            listener
                ?: throw IllegalStateException("handLandmarkerHelperListener must be set when runningMode is LIVE_STREAM.")
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
            handLandmarker = HandLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            listener?.onError("Hand Landmarker failed to initialize. See error logs for details")
            Log.e(
                HandLandmarkerHelper.TAG,
                "MediaPipe failed to load the task with error: ${e.message}"
            )
        } catch (e: RuntimeException) {
            listener?.onError(
                "Hand Landmarker failed to initialize. See error logs for details",
                HandLandmarkerHelper.GPU_ERROR
            )
            Log.e(
                HandLandmarkerHelper.TAG,
                "Hand Landmarker failed to load model with error: ${e.message}"
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

        handLandmarker?.detectAsync(mpImage, frameTime)
        faceLandmarker?.detectAsync(mpImage, frameTime)
        poseLandmarker?.detectAsync(mpImage, frameTime)
    }


    private fun returnLivestreamHandLandMarkerResult(
        result: HandLandmarkerResult,
        input: MPImage,
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        handLandmarkerHelperListener?.onResultsHandLandmarker(
            ResultHandLandmarkerBundle(
                listOf(result),
                inferenceTime,
                input.height,
                input.width
            )
        )
    }

    private fun returnLivestreamHandLandMarkerError(error: RuntimeException) {
        handLandmarkerHelperListener?.onError(error.message ?: "An unknown error has occurred")
    }

    private fun returnLivestreamPoseLandmarkResult(
        result: PoseLandmarkerResult,
        input: MPImage
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        poseLandmarkerHelperListener?.onResultsPoseLandmarker(
            ResultPoseLandmarkerBundle(
                listOf(result),
                inferenceTime,
                input.height,
                input.width
            )
        )
    }

    private fun returnLivestreamPoseLandmarkError(error: RuntimeException) {
        poseLandmarkerHelperListener?.onError(
            error.message ?: "An unknown error has occurred"
        )
    }

    private fun returnLivestreamFaceLandmarkerResult(
        result: FaceLandmarkerResult,
        input: MPImage
    ) {
        if (result.faceLandmarks().size > 0) {
            val finishTimeMs = SystemClock.uptimeMillis()
            val inferenceTime = finishTimeMs - result.timestampMs()

            faceLandmarkerHelperListener?.onResultsFaceLandmarker(
                ResultFaceLandmarkerBundle(
                    result,
                    inferenceTime,
                    input.height,
                    input.width
                )
            )
        } else {
            faceLandmarkerHelperListener?.onError("No face detected")
        }
    }

    private fun returnLivestreamFaceLandmarkerError(error: RuntimeException) {
        faceLandmarkerHelperListener?.onError(
            error.message ?: "An unknown error has occurred"
        )
    }

    companion object {
        const val TAG = "CombinedLandmarkerHelper"
        private const val MP_FACE_LANDMARKER_TASK = "face_landmarker.task"
        private const val MP_HAND_LANDMARKER_TASK = "hand_landmarker.task"
        private const val MP_POSE_LANDMARKER_TASK = "pose_landmarker.task"
        private const val MP_FACE_DETECTION_CONFIDENCE = "face_detection_confidence"

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
        const val DEFAULT_NUM_POSES = 1
        const val MODEL_POSE_LANDMARKER_FULL = 0
        const val MODEL_POSE_LANDMARKER_LITE = 1
        const val MODEL_POSE_LANDMARKER_HEAVY = 2
        const val MODEL_POSE_LANDMARKER_DEFAULT = 3
        const val DEFAULT_NUM_FACES = 1
        const val DEFAULT_NUM_HANDS = 2
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
    }

    data class ResultFaceLandmarkerBundle(
        val results: FaceLandmarkerResult,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    data class ResultHandLandmarkerBundle(
        val results: List<HandLandmarkerResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    data class ResultPoseLandmarkerBundle(
        val results: List<PoseLandmarkerResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    // Helper enums and constants
    enum class LandmarkerType {
        FACE, POSE, HAND
    }

    interface CombinedFaceLandmarkerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResultsFaceLandmarker(result: ResultFaceLandmarkerBundle)
    }

    interface CombinedHandLandmarkerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResultsHandLandmarker(result: ResultHandLandmarkerBundle)
    }

    interface CombinedPoseLandmarkerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResultsPoseLandmarker(result: ResultPoseLandmarkerBundle)
    }
}