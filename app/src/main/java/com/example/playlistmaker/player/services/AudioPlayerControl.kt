package com.example.playlistmaker.player.services

import com.example.playlistmaker.player.ui.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun getPlayerState(): StateFlow<PlayerState>
    fun controlPlayer()
    fun pausePlayer()
    fun release()
    fun showNotification()
    fun hideNotification()
}