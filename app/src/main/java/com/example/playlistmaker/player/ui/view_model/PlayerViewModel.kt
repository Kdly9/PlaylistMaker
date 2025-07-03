package com.example.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.api.MediaInteractor
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.utils.livedata.SingleLiveEvent

class PlayerViewModel(
    private val mediaPlayerInteractor: MediaInteractor
) : ViewModel() {

    private lateinit var previewUrl: String
    private val uiHandler = Handler(Looper.getMainLooper())

    private val playerState = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val showToast = SingleLiveEvent<Boolean>()
    fun observeShowToast(): LiveData<Boolean> = showToast

    companion object {
        private const val UPDATE_TIME = 300L

        fun getFactory(
            mediaPlayerInteractor: MediaInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    mediaPlayerInteractor
                )
            }
        }
    }

    fun setUrl(url: String){
        previewUrl = url
    }

    private val updateCurrentTimeRunnable = object : Runnable {
        override fun run() {
            val state = playerState.value
            if (state is PlayerState.Start) {
                uiHandler.postDelayed(this, UPDATE_TIME)
                playerState.postValue(state.copy(currentPosition = mediaPlayerInteractor.getCurrentPosition()))
            }
        }
    }

    fun preparePlayer() {
        mediaPlayerInteractor.preparePlayer(previewUrl, object : MediaInteractor.Completion {
            override fun completionAction() {
                uiHandler.removeCallbacks(updateCurrentTimeRunnable)
                playerState.postValue(PlayerState.CompletionAction)
            }

            override fun errorPrepare() {
                showToast.postValue(true)
            }

            override fun startPlayer() {
                playerState.postValue(PlayerState.Start(currentPosition = mediaPlayerInteractor.getCurrentPosition()))
            }

            override fun pausePlayer() {
                uiHandler.removeCallbacks(updateCurrentTimeRunnable)
                playerState.postValue(PlayerState.Paused)
            }

            override fun paused() {
                uiHandler.post(updateCurrentTimeRunnable)
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

}