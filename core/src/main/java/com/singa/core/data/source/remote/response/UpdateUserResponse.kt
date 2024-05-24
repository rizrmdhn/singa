package com.singa.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UpdateUserResponse(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("isSignUser")
	val isSignUser: Boolean,

	@field:SerializedName("avatarUrl")
	val avatarUrl: String,

	@field:SerializedName("accountType")
	val accountType: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
