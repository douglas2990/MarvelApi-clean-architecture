package douglas.d2990.io.marvelappstarter.data.model.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import douglas.d2990.io.marvelappstarter.data.model.ThumbnailModel

class MarvelConverters {

    @TypeConverter
    fun fromThumbnail(thumbnailModel: ThumbnailModel): String = Gson().toJson(thumbnailModel)

    @TypeConverter
    fun toThumbnail(thumbnailModel: String): ThumbnailModel =
        Gson().fromJson(thumbnailModel, ThumbnailModel::class.java)
}