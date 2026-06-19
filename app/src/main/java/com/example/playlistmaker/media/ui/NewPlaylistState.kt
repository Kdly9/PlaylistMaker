package com.example.playlistmaker.media.ui

import com.example.playlistmaker.media.domain.model.Playlist

sealed class NewPlaylistState {

    data object CreateState : NewPlaylistState()

    data class EditState(val playlist: Playlist) : NewPlaylistState()
}