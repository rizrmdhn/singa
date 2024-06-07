package com.singa.asl.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerProgress(targetValue: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val shimmer = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val brush = Brush.horizontalGradient(
        colors = listOf(Color.Gray.copy(alpha = 0.9f), Color.White.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.9f)),
        startX = 0f,
        endX = 1000f * shimmer.value
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(targetValue)
            .height(60.dp)
            .background(brush)
    )
}
