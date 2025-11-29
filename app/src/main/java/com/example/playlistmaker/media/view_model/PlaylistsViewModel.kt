package com.example.playlistmaker.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val playlists = MutableLiveData<List<Playlist>>()
    val observePlaylists: LiveData<List<Playlist>> = playlists

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            try {
                playlistInteractor.getAllPlaylists().collect { playlist ->
                    playlists.value = playlist
                }
            } catch (e: Exception) {
                playlists.value = emptyList()
            }
        }
    }
}