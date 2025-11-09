package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.MediaInteractor
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.utils.livedata.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val mediaPlayerInteractor: MediaInteractor
) : ViewModel() {

    private lateinit var previewUrl: String
    private var timerJob: Job? = null


    private val playerState = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val showToast = SingleLiveEvent<Boolean>()
    fun observeShowToast(): LiveData<Boolean> = showToast

    companion object {
        private const val UPDATE_TIME = 300L
    }

    fun setUrl(url: String) {
        previewUrl = url
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
        mediaPlayerInteractor.preparePlayer(previewUrl, object : MediaInteractor.Completion {
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