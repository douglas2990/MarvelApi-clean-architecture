package douglas.d2990.io.marvelappstarter.data.model

import com.google.gson.annotations.SerializedName

data class ThumbnailModel(
    @SerializedName("path")
    val path: String,
    @SerializedName("extension")
    val extension: String
)
