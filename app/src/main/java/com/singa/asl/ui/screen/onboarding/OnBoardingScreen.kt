package com.singa.asl.ui.screen.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.singa.asl.ui.components.OnBoardingItem
import com.singa.asl.ui.components.OnBoardingItems
import kotlinx.coroutines.launch

@Composable
fun OnBoardingScreen() {
    OnBoardingContent()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingContent(
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
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = null
                )
            }

            // Skip Button
            TextButton(
                onClick = {
                    if (pageState.currentPage + 1 < items.size) scope.launch {
                        pageState.scrollToPage(
                            items.size - 1
                        )
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Text(
                    text = "Skip",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        HorizontalPager(
            state = pageState,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) { page ->
            OnBoardingItem(
                items = items[page],
                size = items.size,
                currentPage = page,
                onClickNext = {
                    if (pageState.currentPage + 1 < items.size) scope.launch {
                        pageState.scrollToPage(
                            pageState.currentPage + 1
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnBoardingScreen()
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OnboardingScreenDarkPreview() {
    OnBoardingScreen()
}