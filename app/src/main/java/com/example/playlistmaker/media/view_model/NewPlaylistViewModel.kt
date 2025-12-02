package com.example.playlistmaker.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.NewPlaylistState
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val _screenState = MutableLiveData<NewPlaylistState>()
    val screenState: LiveData<NewPlaylistState> = _screenState

    private var playlistFromStore: Playlist? = null

    fun setPlaylist(playlistId: Long) {
        if (playlistId != -1L) {
            viewModelScope.launch {
                playlistFromStore = playlistInteractor.getPlaylistById(playlistId)
                playlistFromStore?.let {
                    _screenState.value = NewPlaylistState.EditState(
                        playlist = it
                    )
                }
            }
        } else {
            _screenState.value = NewPlaylistState.CreateState
        }
    }

    fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            try {
                if (playlistFromStore != null) {
                    val updPlaylist = playlistFromStore!!.copy(
                        name = playlist.name,
                        description = playlist.description,
                        imagePath = playlist.imagePath
                    )
                    playlistInteractor.updatePlaylist(updPlaylist)
                } else {
                    playlistInteractor.addPlaylist(playlist)
                }
            } catch (_: Exception) {

            }
        }
    }


}
