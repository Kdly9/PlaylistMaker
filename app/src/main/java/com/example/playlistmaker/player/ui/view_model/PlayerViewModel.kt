package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FavoritesTracksInteractor
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.player.services.AudioPlayerControl
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.livedata.SingleLiveEvent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val favouritesTracksInteractor: FavoritesTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var track: Track? = null

    private val playerState = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val trackFavorite = MutableLiveData<Boolean>()
    fun observeTrackFavorite(): LiveData<Boolean> = trackFavorite

    private val showToast = SingleLiveEvent<Boolean>()
    fun observeShowToast(): LiveData<Boolean> = showToast

    private val playlistStat = MutableLiveData<PlaylistState>()
    fun observePlaylistStat(): LiveData<PlaylistState> = playlistStat

    private val playlists = MutableLiveData<List<Playlist>>()
    fun observePlaylists(): LiveData<List<Playlist>> = playlists

    private var audioPlayerControl: AudioPlayerControl? = null

    fun setTrack(track: Track) {
        this.track = track
        isFavourite()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlists.value = playlistInteractor.getPlaylists()
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (favouritesTracksInteractor.getTracks()
                    .first()
                    .any { it.trackId == track?.trackId }
            ) {
                track?.isFavorite = false
                trackFavorite.postValue(false)
                track?.trackId?.let { favouritesTracksInteractor.deleteTrack(it) }
            } else {
                track?.isFavorite = true
                trackFavorite.postValue(true)
                track?.let {
                    favouritesTracksInteractor.addTrack(it)
                }
            }

        }
    }

    private fun isFavourite() {
        viewModelScope.launch {
            if (favouritesTracksInteractor.getTracks()
                    .first()
                    .any { it.trackId == track?.trackId }
            ) {
                trackFavorite.postValue(true)
            } else {
                trackFavorite.postValue(false)
            }
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            if (playlist.trackIds.contains(track.trackId)) {
                playlistStat.postValue(PlaylistState.Exist(name = playlist.name))
            } else {
                playlistInteractor.addTrackToPlaylist(track, playlist)
                playlistStat.postValue(PlaylistState.Added(playlist.name))
            }
        }
    }

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
    }

    fun preparePlayer() {
        viewModelScope.launch {
            audioPlayerControl?.getPlayerState()?.collect {
                playerState.postValue(it)
            }
        }
    }

    fun playbackControl() {
        audioPlayerControl?.controlPlayer()
    }

    fun onPause() {
        audioPlayerControl?.pausePlayer()
    }

    fun showNotification() {
        audioPlayerControl?.showNotification()
    }

    fun hideNotification() {
        audioPlayerControl?.hideNotification()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerControl = null
    }

    sealed interface PlaylistState {
        data class Exist(val name: String) : PlaylistState
        data class Added(val name: String) : PlaylistState
    }

}