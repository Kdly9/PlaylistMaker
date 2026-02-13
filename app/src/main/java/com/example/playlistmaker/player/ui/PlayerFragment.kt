package com.example.playlistmaker.player.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.playlistmaker.player.services.MusicService
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

    private var songUrl = ""
    private var artistName = ""
    private var trackName = ""

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicServiceBinder
            playerViewModel.setAudioPlayerControl(binder.getService())
            playerViewModel.preparePlayer()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerViewModel.removeAudioPlayerControl()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Если выдали разрешение — привязываемся к сервису.
            bindMusicService()
        } else {
            // Иначе просто покажем ошибку
            Toast.makeText(requireContext(), "Can't bind service!", Toast.LENGTH_LONG).show()
        }
    }

    private fun bindMusicService() {
        val stopIntent = Intent(requireContext(), MusicService::class.java)
        requireContext().stopService(stopIntent)
        val intent = Intent(requireContext(), MusicService::class.java).apply {
            putExtra("song_url", songUrl)
            putExtra("artist_name", artistName)
            putExtra("track_name", trackName)
        }
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
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
            songUrl = track.previewUrl
            artistName = track.artistName
            trackName = track.trackName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                bindMusicService()
            }
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

        if (!track.releaseDate.isNullOrEmpty()) {
            binding.yearText.text = track.releaseDate?.substring(0, 4)
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


        playerViewModel.observeTrackFavorite().observe(viewLifecycleOwner){
            if (it) {
                binding.likeButton.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_favourite)
            } else {
                binding.likeButton.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_like)

            }
        }

        playerViewModel.observePlayerState().observe(viewLifecycleOwner) {
            when (it) {
                PlayerState.CompletionAction -> {
                    if(view == null)
                        return@observe
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


        binding.playButton.setUpListener(object : PlaybackButtonView.UpListener {
            override fun onUp() {
                playerViewModel.playbackControl()
            }
        })

        playerViewModel.observePlaylists().observe(viewLifecycleOwner) { playlists ->
            playlistsBottomSheetAdapter.playlists = playlists
        }

    }

    override fun onResume() {
        super.onResume()
        playerViewModel.hideNotification()
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.showNotification()
    }

    override fun onDestroyView() {
        playerViewModel.hideNotification()
        requireContext().unbindService(serviceConnection)
        super.onDestroyView()
        binding.playButton.deleteUpListener()
        _binding = null
    }

}