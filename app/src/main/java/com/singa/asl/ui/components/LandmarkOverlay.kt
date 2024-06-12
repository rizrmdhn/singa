package com.singa.asl.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.singa.core.domain.model.FaceLandmarker
import com.singa.core.domain.model.HandLandmarker
import com.singa.core.domain.model.PoseLandmarker

@Composable
fun LandmarkOverlay(
    modifier: Modifier = Modifier,
    isPoseRequired: Boolean = true,
    poseLandmarks: List<PoseLandmarker>?,
    isFaceRequired: Boolean = true,
    faceLandmarks: FaceLandmarker?,
    isHandRequired: Boolean = true,
    handLandmarks: List<HandLandmarker>?,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        // Define the connections for hand landmarks
        val handConnections = listOf(
            // Palm
            Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4), // Thumb
            Pair(0, 5), Pair(5, 6), Pair(6, 7), Pair(7, 8), // Index finger
            Pair(5, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12), // Middle finger
            Pair(9, 13), Pair(13, 14), Pair(14, 15), Pair(15, 16), // Ring finger
            Pair(13, 17), Pair(0, 17), Pair(17, 18), Pair(18, 19), Pair(19, 20) // Little finger
        )

        if (isPoseRequired) {
            poseLandmarks?.forEach { poseLandmark ->
                poseLandmark.landmarks.forEach { landmarkList ->
                    landmarkList.forEach { landmark ->
                        drawCircle(
                            center = Offset(landmark.x * size.width, landmark.y * size.height),
                            radius = 4f,
                            color = Color.Red
                        )
                    }
                }
            }
        }

        if (isFaceRequired) {
            faceLandmarks?.faceLandmarks?.forEach { landmarkList ->
                landmarkList.forEach { landmark ->
                    drawCircle(
                        center = Offset(landmark.x * size.width, landmark.y * size.height),
                        radius = 4f,
                        color = Color.Blue
                    )
                }
            }
        }

        if (isHandRequired) {
            handLandmarks?.forEach { handLandmark ->
                // Draw lines between hand landmarks
                handLandmark.landmarks.forEach { landmarkList ->
                    handConnections.forEach { (startIdx, endIdx) ->
                        val startLandmark = landmarkList[startIdx]
                        val endLandmark = landmarkList[endIdx]
                        drawLine(
                            start = Offset(startLandmark.x * size.width, startLandmark.y * size.height),
                            end = Offset(endLandmark.x * size.width, endLandmark.y * size.height),
                            color = Color.Green,
                            strokeWidth = 2f
                        )
                    }
                }

                // Draw hand landmarks
                handLandmark.landmarks.forEach { landmarkList ->
                    landmarkList.forEach { landmark ->
                        drawCircle(
                            center = Offset(landmark.x * size.width, landmark.y * size.height),
                            radius = 4f,
                            color = Color.Green
                        )
                    }
                }
            }
        }
    }
}

