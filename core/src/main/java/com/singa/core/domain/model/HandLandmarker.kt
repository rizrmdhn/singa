package com.singa.core.domain.model

data class HandLandmarker(
    val timestampMs: Long,
    val landmarks: List<List<NormalizedLandmark>>,
    val worldLandmarks: List<List<Landmark>> // Assuming Landmark is a similar data class
)