package com.singa.asl.ui.screen.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singa.asl.R
import com.singa.asl.ui.components.CardItem
import com.singa.asl.ui.components.MessageCardLoader
import com.singa.asl.ui.theme.ColorBackgroundWhite
import com.singa.core.data.Resource
import com.singa.core.domain.model.StaticTranslation
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    showDialog: (String, String) -> Unit,
    navigateToDetail: (String) -> Unit,
    viewModel: HistoryScreenViewModel = koinViewModel()
) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    viewModel.state.collectAsState(initial = Resource.Loading()).value.let { state ->
        when (state) {
            is Resource.Empty -> {
                HistoryContent(
                    staticTranslations = emptyList(),
                    isLoading = false,
                    isError = false,
                    errorMessage = "",
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refreshStaticTranslations,
                    onDelete = {
                        viewModel.deleteStaticTranslation(it, showDialog)
                    },
                    navigateToDetail
                )
            }

            is Resource.Error -> {
                HistoryContent(
                    staticTranslations = emptyList(),
                    isLoading = false,
                    isError = true,
                    errorMessage = state.message,
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refreshStaticTranslations,
                    onDelete = {
                        viewModel.deleteStaticTranslation(it, showDialog)
                    },
                    navigateToDetail
                )
            }

            is Resource.Loading -> {
                HistoryContent(
                    staticTranslations = emptyList(),
                    isLoading = true,
                    isError = false,
                    errorMessage = "",
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refreshStaticTranslations,
                    onDelete = {
                        viewModel.deleteStaticTranslation(it, showDialog)
                    },
                    navigateToDetail
                )
            }

            is Resource.Success -> {
                HistoryContent(
                    staticTranslations = state.data,
                    isLoading = false,
                    isError = false,
                    errorMessage = "",
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refreshStaticTranslations,
                    onDelete = {
                        viewModel.deleteStaticTranslation(it, showDialog)
                    },
                    navigateToDetail
                )
            }

            is Resource.ValidationError -> {
                HistoryContent(
                    staticTranslations = emptyList(),
                    isLoading = false,
                    isError = true,
                    errorMessage = state.errors.joinToString { it.message },
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refreshStaticTranslations,
                    onDelete = {
                        viewModel.deleteStaticTranslation(it, showDialog)
                    },
                    navigateToDetail
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryContent(
    staticTranslations: List<StaticTranslation>,
    isLoading: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onDelete: (Int) -> Unit,
    navigateToDetail: (String) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val historyListState = rememberLazyListState()

    Box(
        Modifier
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
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
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.empty),
                            contentDescription = "empty column"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No data available",
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                isError -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .verticalScroll(rememberScrollState()),
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
                    LazyColumn(
                        Modifier.padding(16.dp),
                        state = historyListState
                    ) {
                        items(staticTranslations, key = { it.id }) { conversation ->
                            CardItem(
                                image = R.drawable.mdi_message_badge,
                                title = conversation.title,
                                date = conversation.createdAt,
                                isPending = conversation.status == "pending",
                                onClickCard = {
                                    navigateToDetail(conversation.id.toString())
                                },
                                onDeleteItem = {
                                    onDelete(conversation.id)
                                }
                            )
                        }
                    }
                }
            }

        }

        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                onRefresh()
            }
        }

        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                pullToRefreshState.startRefresh()
            } else {
                pullToRefreshState.endRefresh()
            }
        }

        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .absoluteOffset(y = (-90).dp)
        )
    }
}

