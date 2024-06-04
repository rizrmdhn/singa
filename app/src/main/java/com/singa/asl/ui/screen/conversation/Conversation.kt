package com.singa.asl.ui.screen.conversation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.singa.asl.R
import com.singa.asl.ui.components.ConversationCard
import com.singa.asl.ui.components.ConversationCardLoader
import com.singa.asl.ui.components.MySendInput
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color3
import com.singa.asl.utils.getSpeech
import com.singa.core.data.Resource
import com.singa.core.domain.model.ConversationNode
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun ConversationScreen(
    id: Int,
    onNavigateVideo:(Int)->Unit,
    context: Context = LocalContext.current,
    viewModel: ConversationViewModel = koinViewModel()
) {
    val textMessage by viewModel.textMessageState.collectAsState()
    val isInputFocused by viewModel.isInputFocused.collectAsState()

    viewModel.state.collectAsState(initial = Resource.Loading()).value.let {
        when (it) {
            is Resource.Empty -> {
                ConversationContent(
                    context = context,
                    conversationNode = emptyList(),
                    isLoading = false,
                    isError = false,
                    textMessage = textMessage,
                    isInputFocused = isInputFocused,
                    setInputFocus = viewModel::setInputFocus,
                    onChangeTextMessage = viewModel::setTextMessage,
                    onNavigateVideo = onNavigateVideo
                )
            }

            is Resource.Error -> {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                ConversationContent(
                    context = context,
                    conversationNode = emptyList(),
                    isLoading = false,
                    isError = true,
                    textMessage = textMessage,
                    isInputFocused = isInputFocused,
                    setInputFocus = viewModel::setInputFocus,
                    onChangeTextMessage = viewModel::setTextMessage,
                    onNavigateVideo = onNavigateVideo
                )
            }

            is Resource.Loading -> {
                viewModel.getConversationNodes(id)
                ConversationContent(
                    context = context,
                    conversationNode = emptyList(),
                    isLoading = true,
                    isError = false,
                    textMessage = textMessage,
                    isInputFocused = isInputFocused,
                    setInputFocus = viewModel::setInputFocus,
                    onChangeTextMessage = viewModel::setTextMessage,
                    onNavigateVideo = onNavigateVideo
                )
            }

            is Resource.Success -> {
                ConversationContent(
                    context = context,
                    conversationNode = it.data,
                    isLoading = false,
                    isError = false,
                    textMessage = textMessage,
                    isInputFocused = isInputFocused,
                    setInputFocus = viewModel::setInputFocus,
                    onChangeTextMessage = viewModel::setTextMessage,
                    onNavigateVideo = onNavigateVideo
                )
            }

            is Resource.ValidationError -> {
                Log.e("ConversationScreen", it.errors.toString())
            }

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConversationContent(
    context: Context,
    conversationNode: List<ConversationNode>,
    isLoading: Boolean,
    isError: Boolean,
    textMessage: String,
    isInputFocused: Boolean,
    setInputFocus: (Boolean) -> Unit,
    onChangeTextMessage: (String) -> Unit,
    onNavigateVideo:(Int)->Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            onChangeTextMessage(results?.get(0).toString())
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getSpeech(speechRecognizerLauncher)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                MainScope().launch {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    delay(100)
                    setInputFocus(false)
                }
            }
            .onPreInterceptKeyBeforeSoftKeyboard { keyEvent ->
                if (keyEvent.key.keyCode == 17179869184) {
                    MainScope().launch {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        delay(100)
                        setInputFocus(false)
                    }
                    true
                } else {
                    false
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column {
            when {
                isLoading -> {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .fillMaxHeight(0.87f)
                    ) {
                        ConversationCardLoader("video")
                        ConversationCardLoader("speech")
                    }
                }

                conversationNode.isEmpty() -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize(0.87f)
                    ) {
                        Text(
                            text = "No conversation found",
                            color = Color.Gray
                        )
                    }
                }

                isError -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize(0.87f)
                    ) {
                        Text(
                            text = "An error occurred",
                            color = Color.Red
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight(0.87f)
                    ) {
                        items(conversationNode) { item ->
                            ConversationCard(
                                type = item.type.replaceFirstChar { it.uppercase() },
                                date = item.createdAt,
                                text = item.transcripts,
                                onNavigateToVideo = {
                                    onNavigateVideo(item.conversationTranslationId)
                                }
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                MySendInput(
                    text = textMessage,
                    onTextChange = onChangeTextMessage,
                    placeHolder = "Send a message",
                    keyboardController = keyboardController,
                    focusManager = focusManager,
                    isLoading = false,
                    isFocused = isInputFocused,
                    setInputFocus = setInputFocus,
                    onClick = {},
                )

                if (!isInputFocused) {
                    IconButton(
                        enabled = true,
                        onClick = {
                            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                                Toast.makeText(context, "Speech not Available", Toast.LENGTH_SHORT).show()
                            } else {
                                if (
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    getSpeech(speechRecognizerLauncher)
                                } else {
                                    requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = Color1,
                                shape = CircleShape
                            )
                            .border(
                                width = 4.dp,
                                color = Color3,
                                shape = CircleShape
                            ),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.mdi_microphone),
                            contentDescription = "Favorite",
                            modifier = Modifier.size(30.dp),
                            tint = Color.White
                        )
                    }
                    IconButton(
                        enabled = true,
                        onClick = {
                            /*TODO*/
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = Color1,
                                shape = CircleShape
                            )
                            .border(
                                width = 4.dp,
                                color = Color3,
                                shape = CircleShape
                            ),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.fluent_video_16_filled),
                            contentDescription = "Favorite",
                            modifier = Modifier.size(30.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

