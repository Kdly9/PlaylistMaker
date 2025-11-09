package com.example.playlistmaker.player.ui

sealed class PlayerState {

    data object CompletionAction : PlayerState()

    data class Start(
        val currentPosition: Int
    ) : PlayerState()

    data object Paused : PlayerState()
}