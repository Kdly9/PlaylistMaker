package com.example.playlistmaker.media.data.db

import com.example.playlistmaker.media.data.converter.TrackDbConverter
import com.example.playlistmaker.media.data.db.dao.TrackDao
import com.example.playlistmaker.media.data.db.entity.TrackEntity
import com.example.playlistmaker.media.domain.api.FavouritesTrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouritesTrackRepositoryImpl(
    private val appDatabase: AppDataBase, private val trackDbConvertor: TrackDbConverter
) : FavouritesTrackRepository {
    override suspend fun addTrack(track: Track) {
        val entity = convertToTrackEntity(track).copy(
            addedDate = System.currentTimeMillis()
        )
        appDatabase.trackDao().insertTrack(entity)
    }

    override suspend fun deleteTrack(trackId: String) {
        appDatabase.trackDao().deleteTrack(trackId)
    }

    override fun getTracks(): Flow<List<Track>> = appDatabase.trackDao().getTracks()
        .map { entities -> convertToTrack(entities.sortedByDescending{it.addedDate}) }

    private fun convertToTrackEntity(track: Track): TrackEntity {
        return trackDbConvertor.map(track)
    }

    private fun convertToTrack(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}