package com.singa.asl.ui.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.singa.asl.R
import com.singa.asl.ui.components.CardItem
import com.singa.asl.ui.components.MessageCardLoader
import com.singa.asl.ui.theme.ColorBackgroundWhite
import com.singa.core.data.Resource
import com.singa.core.domain.model.StaticTranslation
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    navigateToDetail: (String) -> Unit,
    viewModel: HistoryScreenViewModel = koinViewModel()
) {
    viewModel.state.collectAsState(initial = Resource.Loading()).value.let { state ->
        when (state) {
            is Resource.Empty -> {
                HistoryContent(
                    staticTranslations = emptyList(),
                    isLoading = false,
                    isError = false,
                    errorMessage = "",
                    navigateToDetail
                )
            }

            is Resource.Error -> {
                HistoryContent(
                    staticTranslations = emptyList(),
                    isLoading = false,
                    isError = true,
                    errorMessage = state.message,
                    navigateToDetail
                )
            }

            is Resource.Loading -> {
                HistoryContent(
                    staticTranslations = emptyList(),
                    isLoading = true,
                    isError = false,
                    errorMessage = "",
                    navigateToDetail
                )
            }

            is Resource.Success -> {
                HistoryContent(
                    staticTranslations = state.data,
                    isLoading = false,
                    isError = false,
                    errorMessage = "",
                    navigateToDetail
                )
            }

            is Resource.ValidationError -> {
                HistoryContent(
                    staticTranslations = emptyList(),
                    isLoading = false,
                    isError = true,
                    errorMessage = state.errors.joinToString { it.message },
                    navigateToDetail
                )
            }
        }
    }

}

@Composable
fun HistoryContent(
    staticTranslations: List<StaticTranslation>,
    isLoading: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    navigateToDetail: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .clip(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp
                )
            )
            .background(
                color = ColorBackgroundWhite
            )
    ) {
        when {
            isLoading  -> {
                LazyColumn(Modifier.padding(16.dp)) {
                    items(10) {
                        MessageCardLoader()
                    }
                }
            }

            staticTranslations.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            isError -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            else -> {
                LazyColumn(Modifier.padding(16.dp)) {
                    items(staticTranslations, key = { it.id }) { conversation ->
                        CardItem(
                            image = R.drawable.mdi_message_badge,
                            title = conversation.title,
                            date = conversation.createdAt,
                            onClickCard = {
                                navigateToDetail(conversation.id.toString())
                            }
                        )
                    }
                }
            }
        }

    }
}

