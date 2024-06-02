package com.singa.asl.ui.screen.home


import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.singa.asl.common.Articles
import com.singa.asl.ui.components.ArticleCard
import com.singa.asl.ui.components.Board
import com.singa.asl.ui.theme.ColorBackgroundWhite

@Composable
fun HomeScreen(showModal: () -> Unit) {
    HomeContent(showModal)
}

@Composable
fun HomeContent(showModal: () -> Unit) {
    val context = LocalContext.current

    Column {
        Spacer(modifier = Modifier.height(12.dp))
        Board(showModal)
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ColorBackgroundWhite,
            shape = RoundedCornerShape(topStartPercent = 8, topEndPercent = 8)
        ) {
            LazyColumn(Modifier.padding(16.dp)) {
                items(Articles, key = { it.id }) { articles ->
                    ArticleCard(
                        data = articles,
                        onClickArticle = {
                            context.apply {
                                startActivity(Intent(Intent.ACTION_VIEW, articles.url))
                            }
                        }
                    )
                }
            }
        }
    }
}



