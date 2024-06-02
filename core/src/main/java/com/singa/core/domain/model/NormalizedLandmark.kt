package com.singa.core.domain.model

import java.util.Optional

data class NormalizedLandmark(
    val x: Float,
    val y: Float,
    val z: Float,
    val visibility: Optional<Float>,
    val presence: Optional<Float>
)