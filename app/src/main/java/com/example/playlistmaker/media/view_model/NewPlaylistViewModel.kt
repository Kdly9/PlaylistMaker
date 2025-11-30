package com.example.playlistmaker.media.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            try {
                playlistInteractor.addPlaylist(playlist)
            } catch (_: Exception) {

            }
        }
    }
}