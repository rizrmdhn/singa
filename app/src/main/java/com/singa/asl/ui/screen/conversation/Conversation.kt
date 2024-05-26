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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import com.singa.asl.R
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color2
import com.singa.asl.ui.theme.Color3
import java.util.Locale

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF4BA6F8)
@Composable
fun ConversationScreen() {
    ConversationContent()
}

@Composable
fun ConversationContent() {
    val context = LocalContext.current
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
            //lazyColumn
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
                    onClick = {})

                IconButton(
                    onClick = {
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

@Composable
fun ConversationCard(
    person: String,
) {
    Column(
        horizontalAlignment = if (person == "You") Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = if (person == "They") Arrangement.SpaceBetween else Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(text = person, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "12:00 PM")
            }
            if (person == "They") {
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_play_circle_filled_24),
                        contentDescription = "Play",
                        modifier = Modifier.padding(0.dp),
                        tint = Color(0xFF34C900)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            colors = CardDefaults.cardColors(
                containerColor = Color(if (person == "You") 0xFF7DBDFA else 0xFFF6F9F8),
                contentColor = if (person == "You") Color.White else Color.Black
            ),
            border = BorderStroke(
                1.5.dp, Color(if (person == "You") 0xFF2E8CE0 else 0xFFD9D9D9)
            ),
        ) {
            Column(Modifier.padding(8.dp)) {
                Text(text = "is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.")
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MySendInput(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    placeHolder: String,
    onClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(0.65f),
        value = text,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        ),
        onValueChange = {
            onTextChange(it)
        },
        placeholder = {
            Text(
                text = placeHolder,
                fontWeight = FontWeight.Normal
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = Color.Black,
            focusedBorderColor = Color1,
            unfocusedBorderColor = Color(0xFFEFEFEF),
        ),
        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            IconButton(onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                onClick()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send"
                )
            }
        },
        maxLines = 4,
    )
}

private fun getSpeechInput(context: Context,speechRecognizerLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
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