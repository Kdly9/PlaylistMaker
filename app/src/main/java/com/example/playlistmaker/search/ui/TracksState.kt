package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface TracksState {

    object Loading : TracksState

    data class Content(
        val tracks: List<Track>
    ) : TracksState

    object Failure : TracksState

    object Empty : TracksState

    data class History(
        val tracks: List<Track>
    ) : TracksState

}