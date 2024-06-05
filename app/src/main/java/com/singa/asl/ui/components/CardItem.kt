package com.singa.asl.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color3
import com.singa.asl.utils.Helpers
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


@Composable
fun CardItem(
    image: Int,
    title: String,
    date: String,
    swipeThreshold: Float = 150f,
    sensitivityFactor: Float = 3f,
    onClickCard: () -> Unit,
    onDeleteItem: () -> Unit = {}
) {
    var offset by remember { mutableFloatStateOf(0f) }
    var dismissLeft by remember { mutableStateOf(false) }
    var isDeleteButtonVisible by remember { mutableStateOf(false) }
    val density = LocalDensity.current.density

    LaunchedEffect(dismissLeft) {
        if (dismissLeft) {
            delay(300)
            dismissLeft = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        // Delete Button
        if (isDeleteButtonVisible) {
            IconButton(
                onClick = {
                    dismissLeft = true
                    onDeleteItem()
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "delete",
                    tint = Color.Red
                )
            }
        }

        // Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            onClick = onClickCard,
            border = BorderStroke(1.dp, color = Color1),
            modifier = Modifier
                .offset { IntOffset(offset.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(onDragEnd = {
                        offset = when {
                            offset < -swipeThreshold -> {
                                isDeleteButtonVisible = true
                                -swipeThreshold
                            }
                            else -> {
                                isDeleteButtonVisible = false
                                0f
                            }
                        }
                    }) { change, dragAmount ->
                        if (!isDeleteButtonVisible) {
                            offset += if (dragAmount > 0) return@detectHorizontalDragGestures else (dragAmount / density) * sensitivityFactor
                            if (change.positionChange() != Offset.Zero) change.consume()
                        }
                    }
                }
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Surface(
                        color = Color3,
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = image),
                            contentDescription = "logo",
                            modifier = Modifier.padding(10.dp),
                            tint = Color1
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = Helpers.convertToUserLocalTime(
                                date,
                                pattern = "EEEE, dd MMMM yyyy"
                            ),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "transcript",
                    modifier = Modifier.size(30.dp),
                    tint = Color1
                )
            }
        }
    }
}
