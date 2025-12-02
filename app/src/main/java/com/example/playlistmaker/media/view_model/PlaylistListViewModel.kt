package com.example.playlistmaker.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.PlaylistState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PlaylistListViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val playlistState = MutableLiveData<PlaylistState>()
    fun observePlaylistState(): LiveData<PlaylistState> = playlistState

    fun loadPlaylistInfo(playlistId: Long) {
        viewModelScope.launch {
            try {
                val playlist = playlistInteractor.getPlaylistById(playlistId)
                if (playlist == null) {
                    playlistState.value = PlaylistState.NotFound
                    return@launch
                }

                val tracks = playlistInteractor.getTracksByIds(playlist.trackIds).first()
                val totalDuration = tracks.sumOf { it.trackTimeMillis.toLong() }

                playlistState.value = PlaylistState.Content(
                    playlist = playlist.copy(tracksCount = tracks.size),
                    tracks = tracks,
                    duration = totalDuration.milliseconds,
                    tracksCount = tracks.size
                )
            } catch (e: Exception) {
                playlistState.value = PlaylistState.Error(e.message ?: "Unknown")
            }
        }
    }


    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            try {
                playlistInteractor.deletePlaylistById(playlist.id)
            } catch (e: Exception) {
                playlistState.postValue(e.message?.let { PlaylistState.Error(it) })
            }
        }
    }

    fun removeTrackFromPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            try {
                playlistInteractor.removeTrackFromPlaylist(
                    trackId = track.trackId,
                    playlist = playlist
                )
                loadPlaylistInfo(playlist.id)
            } catch (e: Exception) {
                playlistState.postValue(e.message?.let { PlaylistState.Error(it) })
            }
        }
    }

}
