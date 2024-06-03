package com.singa.asl.utils

import androidx.camera.core.ImageProxy

interface LandmarkerHelper {
    fun detectLiveStream(image: ImageProxy, isFrontCamera: Boolean)
}

class HandLandmarkerHelperT : LandmarkerHelper {
    override fun detectLiveStream(image: ImageProxy, isFrontCamera: Boolean) {
        // Implementation for hand landmarker detection
    }
}

class FaceLandmarkerHelperT : LandmarkerHelper {
    override fun detectLiveStream(image: ImageProxy, isFrontCamera: Boolean) {
        // Implementation for face landmarker detection
    }
}