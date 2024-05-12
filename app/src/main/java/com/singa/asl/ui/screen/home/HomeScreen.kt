package com.singa.asl.ui.screen.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.singa.asl.ui.components.ArticleCard
import com.singa.asl.ui.components.Board
import com.singa.asl.ui.theme.ColorBackgroundWhite

@Composable
fun HomeScreen() {
    HomeContent()
}

@Composable
fun HomeContent() {
    Column {
        Spacer(modifier = Modifier.height(12.dp))
        Board()
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ColorBackgroundWhite,
            shape = RoundedCornerShape(topStartPercent = 8, topEndPercent = 8)
        ) {
            Column(Modifier.padding(16.dp)) {
                ArticleCard()
                ArticleCard()
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HomeScreenPreview() {
    HomeContent()
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenDarkPreview() {
    HomeContent()
}



