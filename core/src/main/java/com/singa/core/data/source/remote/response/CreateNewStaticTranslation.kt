package com.singa.core.data.source.remote.response

data class CreateNewStaticTranslation(
	val createdAt: String,
	val videoUrl: String,
	val video: String,
	val id: Int,
	val title: String,
	val userId: Int,
	val status: String,
	val updatedAt: String
)

