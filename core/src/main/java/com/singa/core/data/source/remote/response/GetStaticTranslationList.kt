package com.singa.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GetStaticTranslationList(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("videoUrl")
	val videoUrl: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
