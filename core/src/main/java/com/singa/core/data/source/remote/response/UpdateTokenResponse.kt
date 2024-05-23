package com.singa.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UpdateTokenResponse(

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("token")
	val token: String
)
