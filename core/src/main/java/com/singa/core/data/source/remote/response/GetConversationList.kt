package com.singa.core.data.source.remote.response

import com.google.gson.annotations.SerializedName


data class GetConversationListItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
