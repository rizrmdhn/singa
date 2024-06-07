package com.singa.asl.ui.screen.home


import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.singa.asl.BuildConfig
import com.singa.asl.ui.components.ArticleCard
import com.singa.asl.ui.components.ArticlesCardLoader
import com.singa.asl.ui.components.Board
import com.singa.asl.ui.theme.ColorBackgroundWhite
import com.singa.core.data.Resource
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(showModal: () -> Unit) {
    HomeContent(showModal)
}

@Composable
fun HomeContent(showModal: () -> Unit, viewModel: HomeViewModel = koinViewModel()) {
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
            viewModel.state.collectAsState(initial = Resource.Loading()).value.let { state ->
                when (state) {
                    is Resource.Empty -> {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("No Article Available", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    is Resource.Error -> {
                        Log.e("HomeScreen", "Error: ${state.message}")
                    }

                    is Resource.Loading -> {
                        LazyColumn(Modifier.padding(16.dp)) {
                            items(5) {
                                ArticlesCardLoader()
                            }
                        }
                    }

                    is Resource.Success -> {
                        LazyColumn(Modifier.padding(16.dp)) {
                            items(state.data, key = { it.id }) { articles ->
                                ArticleCard(
                                    data = articles,
                                    onClickArticle = {
                                        context.apply {
                                            startActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(
                                                        BuildConfig.ARTICLE_URL + articles.id
                                                    )
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    is Resource.ValidationError -> {
                        Log.e("HomeScreen", "ValidationError")
                    }
                }
            }
        }
    }
}



