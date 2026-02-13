package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

class MediaLibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme(darkTheme = isSystemInDarkTheme()) {
                    MediaLibraryScreen(
                        tracksViewModel = koinViewModel(),
                        playlistsViewModel = koinViewModel(),
                        onTrackClick = { track ->
                            findNavController().navigate(
                                R.id.action_mediaLibraryFragment_to_playerFragment,
                                PlayerFragment.createArgs(track)
                            )
                        },
                        onPlaylistClick = { playlist ->
                            findNavController().navigate(
                                R.id.action_mediaLibraryFragment_to_fragmentPlaylistList,
                                FragmentPlaylistList.createArgs(playlist.id)
                            )
                        },

                        onNewPlaylistClick = {
                            findNavController().navigate(
                                R.id.action_mediaLibraryFragment_to_fragmentNewPlaylist
                            )
                        }
                    )
                }
            }
        }
    }
}