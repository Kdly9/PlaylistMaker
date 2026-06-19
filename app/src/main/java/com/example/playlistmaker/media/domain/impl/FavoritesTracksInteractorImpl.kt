package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.api.FavoritesTracksInteractor
import com.example.playlistmaker.media.domain.api.FavouritesTrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesTracksInteractorImpl (private val repository: FavouritesTrackRepository): FavoritesTracksInteractor {
    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun deleteTrack(id: String) {
        repository.deleteTrack(id)
    }

    override fun getTracks(): Flow<List<Track>> {
        return repository.getTracks()
    }
}