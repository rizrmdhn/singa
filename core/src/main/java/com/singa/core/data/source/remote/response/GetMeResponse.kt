package com.singa.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GetMeResponse(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("isSignUser")
	val isSignUser: Boolean,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("avatarUrl")
	val avatarUrl: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
