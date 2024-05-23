package com.singa.asl.ui.screen.history_detail

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singa.asl.R
import com.singa.asl.ui.components.ExoPlayerView
import com.singa.asl.ui.theme.ColorBackgroundWhite

@Composable
fun HistoryDetailScreen() {
    HistoryDetailContent()
}

@Composable
fun HistoryDetailContent() {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColorBackgroundWhite,
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            ExoPlayerView()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Transcript", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.transcript_text),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        }
    }
}

