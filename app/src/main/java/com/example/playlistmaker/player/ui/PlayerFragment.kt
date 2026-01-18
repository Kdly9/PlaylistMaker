package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.ui.view.PlaybackButtonView
import com.example.playlistmaker.player.ui.view.PlaybackButtonView.ButtonState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.dpToPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val playerViewModel by viewModel<PlayerViewModel>()

    private lateinit var playlistsBottomSheetAdapter: PlaylistsBottomSheetAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val dateFormat by lazy {
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }

    companion object {

        private const val SELECTED_KEY = "selected_key"

        fun createArgs(key: Track): Bundle = bundleOf(SELECTED_KEY to key)
    }

    private fun initPlaylistsAdapter(track: Track) {
        playlistsBottomSheetAdapter = PlaylistsBottomSheetAdapter(
            onPlaylistClickListener = { playlist ->
                playerViewModel.addTrackToPlaylist(track, playlist)
            },
            loadImage = { path ->
                try {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(path)
                        .submit()
                        .get()
                } catch (e: Exception) {
                    null
                }
            }
        )
        binding.recyclerView.apply {
            adapter = playlistsBottomSheetAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playerToolbar.setOnClickListener {
            //setResult(RESULT_OK)
            findNavController().navigateUp()
        }
        val track = arguments?.getParcelable<Track>(SELECTED_KEY)

        if (track != null) {
            playerViewModel.setTrack(track)
            initPlaylistsAdapter(track)
        }

        binding.track.text = track?.trackName
        binding.trackOwner.text = track?.artistName
        binding.durationText.text =
            dateFormat.format(track?.trackTimeMillis?.toLong())

        if (track?.collectionName!!.isEmpty()) {
            binding.albumTitle.visibility = View.GONE
            binding.albumText.visibility = View.GONE
        } else {
            binding.albumText.text = track.collectionName
        }

        if (track.releaseDate.isNotEmpty()) {
            binding.yearText.text = track.releaseDate.substring(0, 4)
        }

        binding.styleText.text = track.primaryGenreName
        binding.countryText.text = track.country

        binding.likeButton.setOnClickListener {
            playerViewModel.onFavoriteClicked()
        }


        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            peekHeight = 0
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.visibility = View.GONE
                        else -> binding.overlay.visibility = View.VISIBLE
                    }
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        playerViewModel.loadPlaylists()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.addButton.setOnClickListener {
            playerViewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }


        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_fragmentNewPlaylist)
                .also {
                    findNavController().currentBackStackEntry
                        ?.savedStateHandle
                        ?.getLiveData<Boolean>("playlist_created")
                        ?.observe(viewLifecycleOwner) { created ->
                            if (created) {
                                playerViewModel.loadPlaylists()
                            }
                        }
                }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }


        playerViewModel.observeShowToast().observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.load_track_error),
                Toast.LENGTH_SHORT
            )
                .show()
        }

        playerViewModel.preparePlayer()

        playerViewModel.observePlayerState().observe(viewLifecycleOwner) {
            when (it) {
                PlayerState.CompletionAction -> {
                    binding.currentTime.text = dateFormat.format(0)
                    binding.playButton.setState(ButtonState.PAUSE)
                }

                PlayerState.Paused -> {
                    binding.playButton.setState(ButtonState.PAUSE)
                }

                is PlayerState.Start -> {
                    binding.currentTime.text = dateFormat.format(it.currentPosition)
                    binding.playButton.setState(ButtonState.PLAY)
                }

                is PlayerState.Favorite -> {
                    if (it.isFavorite) {
                        binding.likeButton.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_favourite)
                    } else {
                        binding.likeButton.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_like)

                    }
                }
            }
        }

        playerViewModel.observePlaylistStat().observe(viewLifecycleOwner) {
            when (it) {
                is PlayerViewModel.PlaylistState.Added -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.track_added_to_playlist, it.name),
                        Toast.LENGTH_SHORT
                    ).show()
                    playerViewModel.loadPlaylists()
                }

                is PlayerViewModel.PlaylistState.Exist -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.track_already_added_to_playlist, it.name),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.mock_image)
            .transform(CenterCrop(), RoundedCorners(dpToPx(8f, requireContext())))
            .into(binding.image)

        binding.playButton.setOnClickListener {
            playerViewModel.playbackControl()
        }
        binding.playButton.setUpListener(object : PlaybackButtonView.UpListener {
            override fun onUp() {
                playerViewModel.playbackControl()
            }
        })

        playerViewModel.observePlaylists().observe(viewLifecycleOwner) { playlists ->
            playlistsBottomSheetAdapter.playlists = playlists
        }

    }

    override fun onPause() {
        super.onPause()
        playerViewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}