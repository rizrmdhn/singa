package com.singa.asl.ui.components

import android.icu.util.TimeZone
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.singa.asl.utils.Helpers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ConversationCard(
    type: String,
    date: String,
    text: String,
    onNavigateToVideo: () -> Unit
) {
    Column(
        horizontalAlignment = if (type == "Speech") Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = if (type == "Video") Arrangement.SpaceBetween else Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(text = type, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Helpers.convertToUserLocalTime(date),
                )
            }
            if (type == "Video") {
                IconButton(
                    onClick = onNavigateToVideo,
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
                containerColor = Color(if (type == "Speech") 0xFF7DBDFA else 0xFFF6F9F8),
                contentColor = if (type == "Speech") Color.White else Color.Black
            ),
            border = BorderStroke(
                1.5.dp, Color(if (type == "Speech") 0xFF2E8CE0 else 0xFFD9D9D9)
            ),
        ) {
            Column(Modifier.padding(8.dp)) {
                Text(
                    text
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ConversationCardPreview() {
    ConversationCard(
        type = "Speech",
        date = "2022-10-10T10:10:10",
        text = "Hello, how are you?",
        onNavigateToVideo = {}
    )
}