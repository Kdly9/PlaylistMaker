package com.example.playlistmaker.player.ui

sealed class PlayerState {

    object CompletionAction : PlayerState()

    data class Start(
        val currentPosition: Int
    ) : PlayerState()

    object Paused : PlayerState()
}