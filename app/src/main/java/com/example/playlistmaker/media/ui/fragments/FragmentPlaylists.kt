package com.example.playlistmaker.media.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.ui.PlaylistsAdapter
import com.example.playlistmaker.media.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylists : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PlaylistsAdapter
    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.emptyLists.visibility = View.VISIBLE
        binding.createPlaylist.setOnClickListener {
            parentFragment?.findNavController()?.navigate(
                R.id.action_mediaLibraryFragment_to_fragmentNewPlaylist,
            )
        }

        adapter = PlaylistsAdapter(
            onPlaylistClick = { playlist ->
                findNavController().navigate(
                    R.id.action_mediaLibraryFragment_to_fragmentPlaylistList,
                    FragmentPlaylistList.createArgs(playlist.id)
                )
            },
            loadImage = { path -> loadImageFromInternalStorage(requireContext(), path) }
        )

        binding.recyclerView.adapter = adapter

        viewModel.observePlaylists.observe(viewLifecycleOwner) { playlists ->
            adapter.submitList(playlists)
            binding.emptyLists.isVisible = playlists.isEmpty()
            binding.recyclerView.isVisible = playlists.isNotEmpty()
        }
    }


    private fun loadImageFromInternalStorage(context: Context, path: String?): Bitmap? {
        if (path.isNullOrEmpty()) return null

        return try {
            BitmapFactory.decodeFile(path)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FragmentPlaylists()
    }
}