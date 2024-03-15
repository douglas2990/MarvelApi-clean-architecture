package douglas.d2990.io.marvelappstarter.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import douglas.d2990.io.marvelappstarter.R
import douglas.d2990.io.marvelappstarter.databinding.FragmentFavoriteCharacterBinding
import douglas.d2990.io.marvelappstarter.ui.adapters.CharacterAdapter
import douglas.d2990.io.marvelappstarter.ui.base.BaseFragment
import douglas.d2990.io.marvelappstarter.ui.state.ResourceState
import douglas.d2990.io.marvelappstarter.ui.toast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteCharacterFragment :
    BaseFragment<FragmentFavoriteCharacterBinding, FavoriteCharacterViewModel>(){
    override val viewModel: FavoriteCharacterViewModel by viewModels()
    private val characterAdapter by lazy { CharacterAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        clickAdapter()
        observer()
    }

    private fun observer() = lifecycleScope.launch{
        viewModel.favorites.collect { resource ->
            when(resource){
                is ResourceState.Success ->{
                    resource.data?.let {
                        binding.tvEmptyList.hide()
                        characterAdapter.characters = it.toList()
                    }
                }
                is ResourceState.Empty ->{
                    binding.tvEmptyList.show()
                }
                else ->{}
            }
        }
    }

    private fun clickAdapter() {
        characterAdapter.setOnclickListener { characterModel ->
            val action = FavoriteCharacterFragmentDirections
                .actionFavoriteCharacterFragmentToDetailsCharacterFragment(characterModel)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() = with(binding){
        rvFavoriteCharacter.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(context)
        }
        ItemTouchHelper(itemTouchHelperCallback()).attachToRecyclerView(rvFavoriteCharacter)
    }

    private fun itemTouchHelperCallback(): ItemTouchHelper.SimpleCallback{
        return object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val character = characterAdapter.getCharacterPosition(viewHolder.adapterPosition)
                viewModel.delete(character).also {
                    toast(getString(R.string.message_delete_character))
                }
            }

        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteCharacterBinding = FragmentFavoriteCharacterBinding.inflate(inflater, container, false)
}