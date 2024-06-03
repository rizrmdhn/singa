package com.singa.asl.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConversationCardLoader(
    type: String,
) {
    Column(
        horizontalAlignment = if (type == "You") Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = if (type == "They") Arrangement.SpaceBetween else Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .width(64.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            shimmerBrush(
                                targetValue = 1300f,
                                showShimmer = true
                            )
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .width(32.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            shimmerBrush(
                                targetValue = 1300f,
                                showShimmer = true
                            )
                        )
                )
            }
            if (type == "They") {
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(24.dp)
                        .clip(
                            RoundedCornerShape(100.dp)
                        )
                        .background(
                            shimmerBrush(
                                targetValue = 1300f,
                                showShimmer = true
                            )
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            colors = CardDefaults.cardColors(
                containerColor = Color(if (type == "You") 0xFF7DBDFA else 0xFFF6F9F8),
                contentColor = if (type == "You") Color.White else Color.Black
            ),
            border = BorderStroke(
                1.5.dp, Color(if (type == "You") 0xFF2E8CE0 else 0xFFD9D9D9)
            ),
        ) {
            FlowRow(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                repeat(20) {
                    val random = Random.nextInt(35, 80)
                    Box(
                        modifier = Modifier
                            .padding(bottom = 8.dp, end = 8.dp)
                            .height(16.dp)
                            .width(random.dp)
                            .clip(
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                shimmerBrush(
                                    targetValue = 1300f,
                                    showShimmer = true
                                )
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConversationCardLoaderPreview() {
    ConversationCardLoader("You")
}

@Preview(showBackground = true)
@Composable
fun ConversationCardLoaderPreview2() {
    ConversationCardLoader("They")
}