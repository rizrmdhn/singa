package com.singa.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GetConversationNode(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("conversationTranslationId")
	val conversationTranslationId: Int,

	@field:SerializedName("videoUrl")
	val videoUrl: String,

	@field:SerializedName("transcripts")
	val transcripts: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("video")
	val video: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("userId")
	val userId: Int,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
