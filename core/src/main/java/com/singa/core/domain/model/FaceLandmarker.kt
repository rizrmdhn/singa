package com.singa.core.domain.model

data class FaceLandmarker(
    val timestampMs: Long,
    val faceLandmarks: List<List<NormalizedLandmark>>
)