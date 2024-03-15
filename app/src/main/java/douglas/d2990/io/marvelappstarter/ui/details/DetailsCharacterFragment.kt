package douglas.d2990.io.marvelappstarter.ui.details

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import douglas.d2990.io.marvelappstarter.R
import douglas.d2990.io.marvelappstarter.data.model.character.CharacterModel
import douglas.d2990.io.marvelappstarter.databinding.FragmentDetailsCharacterBinding
import douglas.d2990.io.marvelappstarter.ui.adapters.ComicAdapter
import douglas.d2990.io.marvelappstarter.ui.base.BaseFragment
import douglas.d2990.io.marvelappstarter.ui.limitDescription
import douglas.d2990.io.marvelappstarter.ui.state.ResourceState
import douglas.d2990.io.marvelappstarter.ui.toast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DetailsCharacterFragment :
    BaseFragment<FragmentDetailsCharacterBinding, DetailsCharacterViewModel>() {
    override val viewModel: DetailsCharacterViewModel by viewModels()

    private val args: DetailsCharacterFragmentArgs by navArgs()
    private val comicAdapter by lazy { ComicAdapter() }
    private lateinit var characterModel: CharacterModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterModel = args.character
        viewModel.fetch(characterModel.id)
        setupRecycleView()
        onLoadedCharacter(characterModel)
        collectObserver()
        descriptionCharacter()
    }

    fun descriptionCharacter() {
        binding.tvDescriptionCharacterDetails.setOnClickListener {
            onShowDialog(characterModel)
        }
    }

    private fun onShowDialog(characterModel: CharacterModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(characterModel.name)
            .setMessage(characterModel.description)
            .setNegativeButton(getString(R.string.close_dialog)){dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun collectObserver() = lifecycleScope.launch{
        viewModel.details.collect { result ->
            when(result){
                is ResourceState.Success -> {
                    binding.progressBarDetail.hide()
                    result.data?.let { values ->
                        if(values.data.result.count() > 0){
                            comicAdapter.comics = values.data.result.toList()
                        }else{
                            toast(getString(R.string.empty_list_comics))
                        }
                    }
                }
                is ResourceState.Error -> {
                    binding.progressBarDetail.hide()
                    result.message?.let { message ->
                        Timber.tag("DetailsCharacter").e("Error -> $message")
                        toast(message)
                    }
                }
                is ResourceState.Loading -> {
                    binding.progressBarDetail.show()
                }
                else ->{}
            }
        }
    }

    private fun onLoadedCharacter(characterModel: CharacterModel) =with(binding) {
       tvNameCharacterDetails.text = characterModel.name
        if(characterModel.description.isEmpty()){
            tvDescriptionCharacterDetails.text =
                requireContext().getString(R.string.text_description_empty)
        }else{
            tvDescriptionCharacterDetails.text = characterModel.description.limitDescription(100)
        }
        Glide.with(requireContext())
            .load(characterModel.thumbnail.path + "." + characterModel.thumbnail.extension)
            .into(imgCharacterDetails)
    }

    private fun setupRecycleView() = with(binding) {
        rvComics.apply {
            adapter = comicAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.favorite -> {
                viewModel.insert(characterModel)
                toast(getString(R.string.saved_successfully))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailsCharacterBinding =
        FragmentDetailsCharacterBinding.inflate(inflater, container, false)
}