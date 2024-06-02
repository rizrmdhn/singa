package com.singa.asl.ui.screen.conversation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.singa.asl.R
import com.singa.asl.ui.components.ConversationCard
import com.singa.asl.ui.components.MySendInput
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color3
import java.util.Locale

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF4BA6F8)
@Composable
fun ConversationScreen(
    context: Context = LocalContext.current
) {
    ConversationContent(
        context = context
    )
}

@Composable
fun ConversationContent(
    context: Context
) {
    var textMessage by remember { mutableStateOf("") }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            textMessage = results?.get(0).toString()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxHeight(0.87f)
            ) {
                ConversationCard("You")
                ConversationCard("They")
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                MySendInput(
                    text = textMessage,
                    onTextChange = {
                        textMessage = it
                    },
                    placeHolder = "Send a message",
                    isLoading = false,
                    onClick = {},
                )

                IconButton(
                    enabled = true,
                    onClick = {
                        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                            // if the intent is not present we are simply displaying a toast message.
                            Toast.makeText(context, "Speech not Available", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            // on below line we are calling a speech recognizer intent
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

                            // on the below line we are specifying language model as language web search
                            intent.putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
                            )

                            // on below line we are specifying extra language as default english language
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

                            // on below line we are specifying prompt as Speak something
                            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Something")

                            // at last we are calling start activity
                            // for result to start our activity.
                            speechRecognizerLauncher.launch(intent)
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


private fun getSpeechInput(
    context: Context,
    speechRecognizerLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    // on below line we are checking if speech
    // recognizer intent is present or not.
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
        // if the intent is not present we are simply displaying a toast message.
        Toast.makeText(context, "Speech not Available", Toast.LENGTH_SHORT).show()
    } else {
        // on below line we are calling a speech recognizer intent
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        // on the below line we are specifying language model as language web search
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        )

        // on below line we are specifying extra language as default english language
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        // on below line we are specifying prompt as Speak something
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Something")

        // at last we are calling start activity
        // for result to start our activity.
        speechRecognizerLauncher.launch(intent)
    }
}