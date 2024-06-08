package com.singa.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GetDetailVideoConversation(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("conversationTranslationId")
	val conversationTranslationId: Int,

	@field:SerializedName("videoUrl")
	val videoUrl: String,

	@field:SerializedName("transcripts")
	val transcripts: List<ConversationTranscriptsItem>,

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

data class ConversationTranscriptsItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("userId")
	val userId: Int,

	@field:SerializedName("conversationNodeId")
	val conversationNodeId: Int,

	@field:SerializedName("timestamp")
	val timestamp: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
