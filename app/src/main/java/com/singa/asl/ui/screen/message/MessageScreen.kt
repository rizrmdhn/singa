package com.singa.asl.ui.screen.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.singa.asl.R
import com.singa.asl.ui.components.CardItem
import com.singa.asl.ui.theme.ColorBackgroundWhite


@Composable
fun MessageScreen(
    onNavigateConversation: () -> Unit
) {
    MessageContent(onNavigateConversation)
}

@Composable
fun MessageContent(onNavigateConversation: () -> Unit) {
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
        LazyColumn(Modifier.padding(16.dp)) {
            items(2) {
                CardItem(
                    image = R.drawable.mdi_message_badge,
                    onClickCard = onNavigateConversation
                )
            }
        }
    }
}