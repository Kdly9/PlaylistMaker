package com.example.playlistmaker.player.domain.api

import android.media.MediaPlayer

interface MediaManager {
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun release()
    fun reset()
    fun getCurrentPosition(): Int
    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener)
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener)
}