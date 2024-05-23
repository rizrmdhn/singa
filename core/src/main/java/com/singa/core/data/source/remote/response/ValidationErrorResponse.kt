package com.singa.core.data.source.remote.response
import com.google.gson.annotations.SerializedName

data class SchemaErrorResponse(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("errors")
    val errors: List<SchemaError>
)

data class SchemaError(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("rule")
    val rule: String,

    @field:SerializedName("field")
    val field: String,

)