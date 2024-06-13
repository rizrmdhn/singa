package com.singa.core.domain.model

data class PoseLandmarker(
    val timestampMs: Long,
    val landmarks: List<List<NormalizedLandmark>>
)