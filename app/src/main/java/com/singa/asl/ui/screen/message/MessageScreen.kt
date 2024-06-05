package com.singa.asl.ui.screen.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singa.asl.R
import com.singa.asl.ui.components.CardItem
import com.singa.asl.ui.components.MessageCardLoader
import com.singa.asl.ui.theme.ColorBackgroundWhite
import com.singa.core.data.Resource
import com.singa.core.domain.model.Conversation
import org.koin.androidx.compose.koinViewModel


@Composable
fun MessageScreen(
    showDialog: (String, String) -> Unit,
    onNavigateConversation: (id: Int) -> Unit,
    viewModel: MessageScreenViewModel = koinViewModel()
) {
    viewModel.state.collectAsState(initial = Resource.Loading()).value.let { state ->
        when (state) {
            is Resource.Empty -> {
                MessageContent(
                    conversations = emptyList(),
                    isLoading = false,
                    isError = false,
                    errorMessage = "",
                    onNavigateConversation,
                    onDeleteConversationNode = { id ->
                        viewModel.deleteConversationNode(id, showDialog)
                    }
                )
            }

            is Resource.Error -> {
                MessageContent(
                    conversations = emptyList(),
                    isLoading = false,
                    isError = true,
                    errorMessage = state.message,
                    onNavigateConversation,
                    onDeleteConversationNode = { id ->
                        viewModel.deleteConversationNode(id, showDialog)
                    }
                )
            }

            is Resource.Loading -> {
                MessageContent(
                    conversations = emptyList(),
                    isLoading = true,
                    isError = false,
                    errorMessage = "",
                    onNavigateConversation,
                    onDeleteConversationNode = { id ->
                        viewModel.deleteConversationNode(id, showDialog)
                    }
                )
            }

            is Resource.Success -> {
                MessageContent(
                    conversations = state.data,
                    isLoading = false,
                    isError = false,
                    errorMessage = "",
                    onNavigateConversation,
                    onDeleteConversationNode = { id ->
                        viewModel.deleteConversationNode(id, showDialog)
                    }
                )
            }

            is Resource.ValidationError -> {
                MessageContent(
                    conversations = emptyList(),
                    isLoading = false,
                    isError = true,
                    errorMessage = state.errors.joinToString { it.message },
                    onNavigateConversation,
                    onDeleteConversationNode = { id ->
                        viewModel.deleteConversationNode(id, showDialog)
                    }
                )
            }
        }
    }
}

@Composable
fun MessageContent(
    conversations: List<Conversation>,
    isLoading: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    onNavigateConversation: (id: Int) -> Unit,
    onDeleteConversationNode: (id: Int) -> Unit,
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

            conversations.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty),
                        contentDescription = "empty column"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.no_conversation),
                        fontSize = 20.sp,
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
                    items(conversations, key = { it.id }) { conversation ->
                        CardItem(
                            image = R.drawable.mdi_message_badge,
                            title = conversation.title,
                            date = conversation.createdAt,
                            onClickCard = {
                                onNavigateConversation(conversation.id)
                            },
                            onDeleteItem = {
                                onDeleteConversationNode(conversation.id)
                            }
                        )
                    }
                }
            }
        }
    }
}