package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.media.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.OnTrackClickListener
import com.example.playlistmaker.search.ui.TracksAdapter
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentFavoriteTracks : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private val favouritesViewModel: FavoriteTracksViewModel by viewModel()

    private val tracksAdapter = TracksAdapter(object : OnTrackClickListener {
        override fun onTrackClick(track: Track) {
            onTrackClickDebounce(track)
        }
    })

    private lateinit var onTrackClickDebounce: (Track) -> Unit


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onTrackClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
                parentFragment?.findNavController()?.navigate(
                    R.id.action_mediaLibraryFragment_to_playerFragment,
                    PlayerFragment.createArgs(track)
                )
            }


        binding.recyclerView.adapter = tracksAdapter

        favouritesViewModel.observeState().observe(viewLifecycleOwner){state ->
            when(state){
                is FavouritesState.Content -> {
                    tracksAdapter.updateData(state.tracks)
                    binding.emptyLib.visibility = View.GONE
                    tracksAdapter.notifyDataSetChanged()
                }
                FavouritesState.Empty -> {
                    tracksAdapter.updateData(emptyList())
                    binding.emptyLib.visibility = View.VISIBLE
                    tracksAdapter.notifyDataSetChanged()
                }
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FragmentFavoriteTracks()

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}