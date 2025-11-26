package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesTrackRepository {
    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(trackId: String)
    fun getTracks(): Flow<List<Track>>
}