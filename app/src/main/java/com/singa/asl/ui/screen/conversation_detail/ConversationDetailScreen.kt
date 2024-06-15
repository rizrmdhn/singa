package com.singa.asl.ui.screen.conversation_detail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.singa.asl.ui.components.ExoPlayerView
import com.singa.asl.ui.components.HistoryDetailLoader
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.ColorBackgroundWhite
import com.singa.asl.utils.timeToMillis
import com.singa.core.data.Resource
import com.singa.core.domain.model.DetailVideoConversation
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConversationDetailScreen(
    translationId: Int,
    conversationId: Int,
    viewModel: ConversationDetailScreenViewModel = koinViewModel()
) {
    viewModel.state.collectAsState(initial = Resource.Loading()).value.let { state ->
        when (state) {
            is Resource.Empty -> {
                Log.i("ConversationDetailScreen", "Empty")
            }
            is Resource.Error -> {
                Log.i("ConversationDetailScreen", "Error: ${state.message}")
            }
            is Resource.Loading -> {
                viewModel.getVideoDetailTranslations(conversationId, translationId)
                HistoryDetailLoader()
            }
            is Resource.Success -> {
                HistoryDetailContent(
                    videoTranslationDetail = state.data
                )
            }
            is Resource.ValidationError -> TODO()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryDetailContent(
    videoTranslationDetail: DetailVideoConversation
) {
    val context = LocalContext.current

    var mutableTimeStamp by remember { mutableLongStateOf(0L) }
    val exoPlayer = ExoPlayer.Builder(context).build()

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColorBackgroundWhite,
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            ExoPlayerView(
                exoPlayer = exoPlayer,
                videoUrl = videoTranslationDetail.videoUrl,
                timeStamp = {
                    mutableTimeStamp = it
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow {
                videoTranslationDetail.transcript?.forEach { transcript ->

                    val timestamp = timeToMillis(transcript.timestamp)
                    val isSelected = kotlin.math.abs(timestamp - mutableTimeStamp) <= 1000L
                    Text(
                        text = transcript.text,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(4.dp)
                            .then(
                                if (
                                    isSelected
                                ) Modifier
                                    .background(
                                        Color1.copy(alpha = 0.1f),
                                    )
                                    .padding(4.dp)
                                else Modifier.padding(4.dp)
                            )
                            .clickable {
                                mutableTimeStamp = timestamp
                                exoPlayer.seekTo(timestamp)
                            }
                    )
                }
            }

        }
    }
}

