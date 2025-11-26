package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FavoritesTracksInteractor
import com.example.playlistmaker.player.domain.api.MediaInteractor
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.livedata.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val mediaPlayerInteractor: MediaInteractor,
    private val favouritesTracksInteractor: FavoritesTracksInteractor
) : ViewModel() {

    private var track: Track? = null
    private var timerJob: Job? = null

    private val playerState = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val showToast = SingleLiveEvent<Boolean>()
    fun observeShowToast(): LiveData<Boolean> = showToast

    companion object {
        private const val UPDATE_TIME = 300L
    }

    fun setTrack(track: Track) {
        this.track = track
        isFavourite()
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (favouritesTracksInteractor.getTracks()
                    .first()
                    .any { it.trackId == track?.trackId }
            ) {
                track?.isFavorite = false
                playerState.postValue(PlayerState.Favorite(false))
                track?.trackId?.let { favouritesTracksInteractor.deleteTrack(it) }
            } else {
                track?.isFavorite = true
                playerState.postValue(PlayerState.Favorite(true))
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
                playerState.postValue(PlayerState.Favorite(true))
            } else {
                playerState.postValue(PlayerState.Favorite(false))
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayerInteractor.mediaIsPlaying()) {
                delay(UPDATE_TIME)
                playerState.postValue(PlayerState.Start(currentPosition = mediaPlayerInteractor.getCurrentPosition()))
            }
        }
    }

    fun preparePlayer() {
        track?.let {
            mediaPlayerInteractor.preparePlayer(
                it.previewUrl,
                object : MediaInteractor.Completion {
                    override fun completionAction() {
                        playerState.postValue(PlayerState.CompletionAction)
                        timerJob?.cancel()
                    }

                    override fun errorPrepare() {
                        showToast.postValue(true)
                    }

                    override fun startPlayer() {
                        playerState.postValue(PlayerState.Start(currentPosition = mediaPlayerInteractor.getCurrentPosition()))
                    }

                    override fun pausePlayer() {
                        timerJob?.cancel()
                        playerState.postValue(PlayerState.Paused)
                    }

                    override fun paused() {
                        startTimer()
                    }
                })
        }
    }

    fun playbackControl() {
        mediaPlayerInteractor.playbackControl()
    }

    fun onPause() {
        mediaPlayerInteractor.pausePlayer()
    }

    fun onRelease() {
        mediaPlayerInteractor.release()
    }

    fun onReset() {
        mediaPlayerInteractor.reset()
    }


}