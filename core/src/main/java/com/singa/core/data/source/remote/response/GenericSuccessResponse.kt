package com.singa.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GenericSuccessResponse(

	@field:SerializedName("meta")
	val meta: Meta
)


