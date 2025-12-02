package com.example.playlistmaker.media.ui

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlin.time.Duration

sealed class PlaylistState {
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val duration: Duration,
        val tracksCount: Int
    ) : PlaylistState()

    data object NotFound : PlaylistState()

    data class Error(val message: String) : PlaylistState()
}