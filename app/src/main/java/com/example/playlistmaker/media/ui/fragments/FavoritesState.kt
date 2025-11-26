package com.example.playlistmaker.media.ui.fragments

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavouritesState {

    data object Empty : FavouritesState

    data class Content(
        val tracks: List<Track>
    ) : FavouritesState

}