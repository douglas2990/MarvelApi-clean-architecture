package douglas.d2990.io.marvelappstarter.ui.list

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
class ListCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository
) : ViewModel() {

    private val _list = MutableStateFlow<ResourceState<CharacterModelResponse>>(ResourceState.Loading())
    val list: StateFlow<ResourceState<CharacterModelResponse>> = _list

    init {
        fetch()
    }

    private fun fetch() = viewModelScope.launch{
        safeFetch()
    }

    private suspend fun safeFetch() {
        try {
            val response = repository.list()
            _list.value = handlerResponse(response)
        }catch (t: Throwable){
            when(t){
                is IOException -> _list.value = ResourceState.Error("Erro de conexão com a internet")
                else -> _list.value = ResourceState.Error("Falha na conversão de dados")
            }
        }
    }

    private fun handlerResponse(response: Response<CharacterModelResponse>): ResourceState<CharacterModelResponse> {
        if (response.isSuccessful){
            response.body()?.let { values ->
                return ResourceState.Success(values)
            }
        }
        return ResourceState.Error(response.message())
    }
}