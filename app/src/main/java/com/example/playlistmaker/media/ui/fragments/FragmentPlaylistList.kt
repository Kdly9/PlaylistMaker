package com.example.playlistmaker.media.ui.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistListBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.PlaylistState
import com.example.playlistmaker.media.view_model.PlaylistListViewModel
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.OnTrackClickListener
import com.example.playlistmaker.search.ui.TracksAdapter
import com.example.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

class FragmentPlaylistList : Fragment() {
    private var _binding: FragmentPlaylistListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistListViewModel by viewModel()

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val tracksAdapter = TracksAdapter(object : OnTrackClickListener {
        override fun onTrackClick(track: Track) {
            onTrackClickDebounce(track)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
                parentFragment?.findNavController()?.navigate(
                    R.id.action_fragmentPlaylistList_to_playerFragment,
                    PlayerFragment.createArgs(track)
                )
            }
        tracksAdapter.setOnLongClickListener { track ->
            showDeleteDialog(track)
        }

        val playlistId = arguments?.getLong(PLAYLIST_ID) ?: -1L

        binding.recyclerView.adapter = tracksAdapter

        binding.toolBar.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.observePlaylistState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Content -> showContent(state)
                is PlaylistState.Error -> { Toast.makeText(requireContext(), getString(R.string.error) + state.message, Toast.LENGTH_LONG).show()}
                PlaylistState.NotFound -> { Toast.makeText(requireContext(), getString(R.string.playlist_not_found), Toast.LENGTH_LONG).show()}
            }
        }

        viewModel.loadPlaylistInfo(playlistId)

        binding.shareButton.setOnClickListener {
            val state = viewModel.observePlaylistState().value
            if (state is PlaylistState.Content) {
                if (state.tracks.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.empty_playlist_share_message),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    sharePlaylist(state.playlist, state.tracks)
                }
            }
        }

        binding.menuButton.setOnClickListener {
            showMenuBottomSheet()
        }

        binding.share.setOnClickListener {
            shareCurrentPlaylist()
            hideMenuBottomSheet()
        }

        binding.edit.setOnClickListener {
            editCurrentPlaylist()
            hideMenuBottomSheet()
        }

        binding.delete.setOnClickListener {
            hideMenuBottomSheet()
            showDeletePlaylistDialog()
        }
    }

    private fun editCurrentPlaylist() {
        val playlist = (viewModel.observePlaylistState().value as? PlaylistState.Content)?.playlist ?: return
        findNavController().navigate(
            R.id.action_fragmentPlaylistList_to_fragmentNewPlaylist,
            FragmentNewPlaylist.createArgs(playlist.id)
        )
    }

    private fun showMenuBottomSheet() {
        val state = viewModel.observePlaylistState().value as? PlaylistState.Content ?: return

        binding.background.isVisible = true
        binding.menuBottomSheet.isVisible = true

        binding.behaviorName.text = state.playlist.name
        binding.tracksCount.text = requireContext().resources.getQuantityString(
            R.plurals.track,
            state.tracks.size,
            state.tracks.size
        )

        state.playlist.imagePath?.let { imagePath ->
            try {
                val file = if (imagePath.contains("playlist_covers")) {
                    File(imagePath)
                } else {
                    File(requireContext().filesDir, "playlist_covers/$imagePath")
                }
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    binding.poster.setImageBitmap(bitmap)
                } else {
                    binding.poster.setImageResource(R.drawable.mock_image)
                }
            } catch (e: Exception) {
                binding.poster.setImageResource(R.drawable.mock_image)
            }
        } ?: run {
            binding.poster.setImageResource(R.drawable.mock_image)
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideMenuBottomSheet()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun hideMenuBottomSheet() {
        binding.background.isVisible = false
        binding.menuBottomSheet.isVisible = false
    }

    private fun shareCurrentPlaylist() {
        val state = viewModel.observePlaylistState().value as? PlaylistState.Content ?: return
        sharePlaylist(state.playlist, state.tracks)
    }

    private fun showDeletePlaylistDialog() {
        val playlist = (viewModel.observePlaylistState().value as? PlaylistState.Content)?.playlist ?: return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist_title, playlist.name))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deletePlaylist(playlist)
                findNavController().navigateUp()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun showContent(state: PlaylistState.Content) {
        with(binding) {
            playlistName.text = state.playlist.name
            if (state.playlist.description != null) {
                playlistDescription.isVisible = true
                playlistDescription.text = state.playlist.description
            }

            minutes.text = String.format(
                Locale.getDefault(),
                getString(R.string.duration_format),
                state.duration.inWholeMinutes
            )
            counts.text = requireContext().resources.getQuantityString(
                R.plurals.track,
                state.tracksCount,
                state.tracksCount
            )

            state.playlist.imagePath?.let { imagePath ->
                loadPlaylistImage(imagePath)
            } ?: run {
                imagePlaylist.setImageResource(R.drawable.mock_image)
            }

            if (state.tracks.isEmpty()) {
                noTracks.isVisible = true
                recyclerView.isVisible = false
            } else {
                noTracks.isVisible = false
                recyclerView.isVisible = true
                tracksAdapter.updateData(state.tracks)
                tracksAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun loadPlaylistImage(imagePath: String) {
        try {
            val file = if (imagePath.contains("playlist_covers")) {
                File(imagePath)
            } else {
                File(requireContext().filesDir, "playlist_covers/$imagePath")
            }

            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    binding.imagePlaylist.setImageBitmap(bitmap)
                    return
                }
            }
            binding.imagePlaylist.setImageResource(R.drawable.mock_image)
        } catch (e: Exception) {
            binding.imagePlaylist.setImageResource(R.drawable.mock_image)
        }
    }


    private fun showDeleteDialog(track: Track) {
        val currentPlaylist = (viewModel.observePlaylistState().value as? PlaylistState.Content)?.playlist

        binding.background.isVisible = true

        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_track_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (currentPlaylist != null) {
                    viewModel.removeTrackFromPlaylist(track, currentPlaylist)
                }
                binding.background.isVisible = false
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                binding.background.isVisible = false
            }
            .setOnDismissListener {
                binding.background.isVisible = false
            }
            .show()
    }

    private fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        val state = viewModel.observePlaylistState().value
        if (state is PlaylistState.Content) {
            if (state.tracks.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.empty_playlist_share_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val shareText = buildShareText(playlist, tracks)

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }

                startActivity(
                    Intent.createChooser(
                        shareIntent,
                        getString(R.string.share_playlist_title)
                    )
                )
            }
        }

    }

    private fun buildShareText(playlist: Playlist, tracks: List<Track>): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append(playlist.name).append("\n")

        playlist.description?.let {
            stringBuilder.append(it).append("\n")
        }

        stringBuilder.append(
            requireContext().resources.getQuantityString(
                R.plurals.track,
                tracks.size,
                tracks.size
            )
        ).append("\n\n")

        tracks.forEachIndexed { index, track ->
            stringBuilder.append(
                "${index + 1}. ${track.artistName} - ${track.trackName} (${
                    formatTrackDuration(
                        track.trackTimeMillis.toLong()
                    )
                })\n"
            )
        }

        return stringBuilder.toString()
    }

    private fun formatTrackDuration(millis: Long?): String {
        if (millis == null) return "0:00"

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)

        return String.format(Locale.getDefault(), getString(R.string.tracks_duration_format), minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PLAYLIST_ID = "playlistId"
        fun createArgs(id: Long): Bundle = Bundle().apply {
            putLong(PLAYLIST_ID, id)
        }

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}