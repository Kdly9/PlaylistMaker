package com.example.playlistmaker.player.domain.api

interface MediaInteractor {
    fun preparePlayer(url: String, completionListener: Completion)
    fun startPlayer()
    fun pausePlayer()
    fun release()
    fun reset()
    fun playbackControl()
    fun getCurrentPosition(): Int
    fun setUpdateCurrentTimeListener(listener: (String) -> Unit)
    fun mediaIsPlaying(): Boolean
    interface Completion {
        fun completionAction()
        fun errorPrepare()
        fun startPlayer()
        fun pausePlayer()
        fun paused()
    }
}