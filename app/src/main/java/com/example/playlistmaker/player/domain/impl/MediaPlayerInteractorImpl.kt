package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.MediaInteractor
import com.example.playlistmaker.player.domain.api.MediaManager

class MediaPlayerInteractorImpl(private val mediaManager: MediaManager) : MediaInteractor {
    private var playerState = STATE_DEFAULT
    private var listener: MediaInteractor.Completion? = null
    private var updateCurrentTimeListener: ((String) -> Unit)? = null
    override fun preparePlayer(url: String, completionListener: MediaInteractor.Completion) {
        listener = completionListener
        try {
            mediaManager.preparePlayer(url)
            mediaManager.setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            mediaManager.setOnCompletionListener {
                playerState = STATE_PREPARED
                listener?.completionAction()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            playerState = STATE_ERROR
            listener?.errorPrepare()
        }
    }

    override fun removeCompletionListener(){
        listener = null
    }

    override fun startPlayer() {
        if (playerState != STATE_ERROR) {
            mediaManager.startPlayer()
            listener?.startPlayer()
            playerState = STATE_PLAYING
        }
    }

    override fun pausePlayer() {
        if (playerState != STATE_ERROR) {
            if (playerState == STATE_PLAYING) {
                mediaManager.pausePlayer()
                listener?.pausePlayer()
                playerState = STATE_PAUSED
            }
        }
    }

    override fun release() {
        mediaManager.release()
    }

    override fun reset() {
        mediaManager.reset()
    }

    override fun stop() {
        mediaManager.stop()
    }

    override fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                listener?.paused()
            }

            STATE_ERROR -> {
                listener?.errorPrepare()
            }
        }
    }

    override fun getCurrentPosition(): Int {
        return if (playerState == STATE_PLAYING || playerState == STATE_PREPARED || playerState == STATE_PAUSED) {
            mediaManager.getCurrentPosition()
        } else {
            0
        }
    }

    override fun setUpdateCurrentTimeListener(listener: (String) -> Unit) {
        this.updateCurrentTimeListener = listener
    }

    override fun mediaIsPlaying(): Boolean {
        return if (playerState == STATE_PLAYING || playerState == STATE_PREPARED || playerState == STATE_PAUSED) {
            mediaManager.isPlaying()
        } else {
            false
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val STATE_ERROR = 4
        private const val UPDATE_TIME = 300L
    }
}