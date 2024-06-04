package com.singa.asl.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.singa.asl.R
import com.singa.core.domain.model.Articles


@Composable
fun ArticleCard(
    data:Articles,
    onClickArticle: () -> Unit
) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        onClick = onClickArticle,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF6F6F6),
            contentColor = Color.Black
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                SubcomposeAsyncImage(
                    model = data.imageUrl,
                    contentDescription = "Article Image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.width(120.dp).height(80.dp)
                ){
                    when (this.painter.state) {
                        is AsyncImagePainter.State.Loading -> {
                            Box(
                                modifier = Modifier
                                    .background(
                                        shimmerBrush(
                                            targetValue = 1300f,
                                            showShimmer = true
                                        )
                                    )
                                    .size(30.dp)
                                    .padding(top = 10.dp)
                            )
                        }

                        is AsyncImagePainter.State.Success -> {
                            SubcomposeAsyncImageContent()
                        }

                        is AsyncImagePainter.State.Error -> {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_background),
                                contentDescription = "image",
                            )
                        }

                        is AsyncImagePainter.State.Empty -> {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_background),
                                contentDescription = "image",
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = data.title,
                    fontSize = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.description,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
