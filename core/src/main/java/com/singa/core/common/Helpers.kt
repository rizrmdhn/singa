package com.singa.core.common

object Helpers {

    fun mediumRestaurantImage(pictureId: String): String {
        return "https://restaurant-api.dicoding.dev/images/medium/$pictureId"
    }

    fun avatarGenerator(username: String): String {
        return "https://ui-avatars.com/api/?name=$username&length=1&background=random&size=128"
    }
}