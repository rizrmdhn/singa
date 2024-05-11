package com.singa.asl.ui.screen.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singa.asl.ui.components.OnBoardingIndicators
import com.singa.asl.ui.components.OnBoardingItem
import com.singa.asl.ui.components.OnBoardingItems
import com.singa.asl.ui.theme.SingaTheme
import kotlinx.coroutines.launch

@Composable
fun OnBoardingScreen(
    onNavigateToWelcome: () -> Unit
) {
    OnBoardingContent(
        onNavigateToWelcome = onNavigateToWelcome
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingContent(
    onNavigateToWelcome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = OnBoardingItems.getItems()
    val scope = rememberCoroutineScope()
    val pageState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { 3 }
    )

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header OnBoardingContent
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Back button
            IconButton(
                onClick = {
                    if (pageState.currentPage + 1 > 1) scope.launch {
                        pageState.scrollToPage(
                            pageState.currentPage - 1
                        )
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color(0xFF4BA6F8)
                ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = null,
                )
            }

            // Skip Button
            TextButton(
                onClick = {
                    onNavigateToWelcome()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF4BA6F8)
                ),
                modifier = Modifier
                    .align(Alignment.CenterEnd),
            ) {
                Text(
                    text = "Skip",
                )
            }
        }

        HorizontalPager(
            state = pageState,
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .fillMaxWidth()
        ) { page ->
            OnBoardingItem(
                items = items[page],
            )
        }

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
                repeat(items.size) {
                    OnBoardingIndicators(
                        isSelected = it == pageState.currentPage,
                        onTapped = {
                            scope.launch {
                                pageState.scrollToPage(it)
                            }
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(50.dp)
            )

            Button(
                onClick = {
                    if (pageState.currentPage + 1 < items.size) scope.launch {
                        pageState.scrollToPage(
                            pageState.currentPage + 1
                        )
                    }

                    if (pageState.currentPage + 1 == items.size) {
                        onNavigateToWelcome()
                    }
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
fun OnboardingScreenPreview() {
    SingaTheme {
        OnBoardingScreen(
            onNavigateToWelcome = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OnboardingScreenDarkPreview() {
    SingaTheme {
        OnBoardingScreen(
            onNavigateToWelcome = {}
        )
    }
}