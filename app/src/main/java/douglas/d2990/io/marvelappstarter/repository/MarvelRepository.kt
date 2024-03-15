package douglas.d2990.io.marvelappstarter.repository

import douglas.d2990.io.marvelappstarter.data.model.character.CharacterModel
import douglas.d2990.io.marvelappstarter.data.model.local.MarvelDao
import douglas.d2990.io.marvelappstarter.data.remote.ServiceApi
import javax.inject.Inject

class MarvelRepository @Inject constructor(
    private val api:ServiceApi,
    private val dao: MarvelDao
) {
    suspend fun list(nameStartsWith: String? = null) = api.list(nameStartsWith)
    suspend fun getComics(characterId: Int) = api.getComics(characterId)

    suspend fun insert(characterModel: CharacterModel) = dao.insert(characterModel)
    fun getAll() = dao.getAll()
    suspend fun delete(characterModel: CharacterModel) = dao.delete(characterModel)
}