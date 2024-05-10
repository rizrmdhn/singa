package com.singa.asl.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singa.asl.R

class OnBoardingItems(
    val image: Int,
    val title: Int,
    val description: Int
) {
    companion object {
        fun getItems() = listOf(
            OnBoardingItems(
                image = com.singa.asl.R.drawable.onboarding_1,
                title = R.string.onboarding_1_title,
                description = R.string.onboarding_1_desc
            ),
            OnBoardingItems(
                image = com.singa.asl.R.drawable.onboarding_2,
                title = R.string.onboarding_2_title,
                description = R.string.onboarding_2_desc
            ),
            OnBoardingItems(
                image = com.singa.asl.R.drawable.onboarding_3,
                title = R.string.onboarding_3_title,
                description = R.string.onboarding_3_desc
            )
        )
    }
}

@Composable
fun OnBoardingItem(
    items: OnBoardingItems,
    size: Int,
    currentPage: Int,
    onClickNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(
                    id = items.image
                ),
                contentDescription = "Image1",
                modifier = Modifier.padding(start = 50.dp, end = 50.dp)
            )

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            Text(
                text = stringResource(
                    id = items.title
                ),
                style = MaterialTheme.typography.headlineMedium,
                // fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = stringResource(
                    id = items.description
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp),
                letterSpacing = 1.sp,
            )
        }

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        // Bottom OnBoardingContent
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Indicators
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(size) {
                    OnBoardingIndicators(isSelected = it == currentPage)
                }
            }

            Spacer(
                modifier = Modifier.height(50.dp)
            )

            Button(
                onClick = {
                    onClickNext()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Continue",
                    fontSize = 20.sp,
                    color = Color.White
                )
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingItemPreview() {
    OnBoardingItem(
        items = OnBoardingItems.getItems()[0],
        size = OnBoardingItems.getItems().size,
        currentPage = 0,
        onClickNext = {}
    )
}