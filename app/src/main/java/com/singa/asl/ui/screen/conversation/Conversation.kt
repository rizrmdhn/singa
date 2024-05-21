package com.singa.asl.ui.screen.conversation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.singa.asl.R
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color2

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF4BA6F8)
@Composable
fun ConversationScreen() {
    ConversationContent()
}

@Composable
fun ConversationContent() {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column {
            //lazyColumn
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxHeight(0.85f)
            ) {
                ConversationCard("You")
                ConversationCard("They")
            }

            ConversationAction()
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
            border = BorderStroke(1.5.dp, Color(if (person == "You") 0xFF2E8CE0 else 0xFFD9D9D9))
        ) {
            Column(Modifier.padding(8.dp)) {
                Text(text = "is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.")
            }
        }
    }
}


@Composable
fun ConversationAction() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .padding(16.dp),
            color = Color1,
            contentColor = Color.White,
            border = BorderStroke(3.dp, Color2),
            shape = shapes.medium
        ) {
            Row(Modifier.padding(8.dp)){
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.fluent_video_16_filled),
                        contentDescription = "Video"
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.mdi_microphone),
                        contentDescription = "Mic"
                    )
                }
            }
        }
    }
}