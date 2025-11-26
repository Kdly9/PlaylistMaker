package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesTracksInteractor {
    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(id: String)
    fun getTracks(): Flow<List<Track>>
}