package com.singa.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class CreateNewSpeechConversation(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("transcript")
	val transcript: Transcript,

	@field:SerializedName("conversationTranslationId")
	val conversationTranslationId: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("userId")
	val userId: Int,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class Transcript(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("userId")
	val userId: Int,

	@field:SerializedName("conversationNodeId")
	val conversationNodeId: Int,

	@field:SerializedName("timestamp")
	val timestamp: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
