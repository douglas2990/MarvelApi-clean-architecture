package douglas.d2990.io.marvelappstarter.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import douglas.d2990.io.marvelappstarter.data.model.character.CharacterModelResponse
import douglas.d2990.io.marvelappstarter.repository.MarvelRepository
import douglas.d2990.io.marvelappstarter.ui.state.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SearchCharacterViewModel @Inject constructor(
        private val repository: MarvelRepository
) : ViewModel(){

    private val _searchCharacter =
        MutableStateFlow<ResourceState<CharacterModelResponse>>(ResourceState.Empty())
    val searchCharacter: StateFlow<ResourceState<CharacterModelResponse>> = _searchCharacter

    fun fetch(nameStartWith: String) = viewModelScope.launch {
        safeFetch(nameStartWith)
    }

    private suspend fun safeFetch(nameStartWith: String){
        _searchCharacter.value = ResourceState.Loading()
        try{
            val response = repository.list(nameStartWith)
            _searchCharacter.value = handleResponse(response)
        }catch (t: Throwable){
            when(t){
                is IOException -> _searchCharacter.value = ResourceState.Error("Erro na rede")
                else -> _searchCharacter.value = ResourceState.Error("Erro na conversão")
            }
        }
    }

    private fun handleResponse(response: Response<CharacterModelResponse>): ResourceState<CharacterModelResponse> {
        if (response.isSuccessful){
            response.body()?.let { values ->
                return ResourceState.Success(values)
            }
        }
        return ResourceState.Error(response.message())
    }
}