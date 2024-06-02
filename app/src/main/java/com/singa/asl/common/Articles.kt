package com.singa.asl.common

import android.net.Uri

data class ArticleItem(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val url: Uri
)

val Articles = listOf<ArticleItem>(
    ArticleItem(
        id = 1,
        title = "Google to pay 700 million in case of cancer",
        description = "is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\\'s standard dummy text",
        imageUrl = "https://picsum.photos/200/300",
        url = Uri.parse("https://www.google.com")
    ),
    ArticleItem(
        id = 2,
        title = "Google to pay 700 million in case of cancer",
        description = "is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\\'s standard dummy text",
        imageUrl = "https://picsum.photos/200/300",
        url = Uri.parse("https://www.google.com")
    ),
    ArticleItem(
        id = 3,
        title = "Google to pay 700 million in case of cancer",
        description = "is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\\'s standard dummy text",
        imageUrl = "https://picsum.photos/200/300",
        url = Uri.parse("https://www.google.com")
    ),
)