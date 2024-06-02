package com.singa.asl.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.singa.asl.ui.theme.Color1


@Preview
@Composable
fun MessageCardLoader() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, color = Color1),
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .height(64.dp)
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
                Column {
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth(0.5f)
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
                    Spacer(modifier = Modifier.size(8.dp))
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth(0.5f)
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
            Box(
                modifier = Modifier
                    .height(32.dp)
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
    }
}