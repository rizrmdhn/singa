import com.google.gson.annotations.SerializedName

data class GithubErrorResponse(
    @SerializedName("meta")
    val meta: Meta
)

data class Meta(
    @SerializedName("code")
    val code: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)
