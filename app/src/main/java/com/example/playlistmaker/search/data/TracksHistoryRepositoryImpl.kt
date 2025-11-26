package com.example.playlistmaker.search.data

import com.example.playlistmaker.media.data.db.AppDataBase
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.storage.TrackStorage
import com.example.playlistmaker.search.domain.api.TracksHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class TracksHistoryRepositoryImpl(private val localStorage: TrackStorage, private val appDatabase: AppDataBase) :
    TracksHistoryRepository {

    override fun saveTracks(saveTracks: List<Track>) {
        localStorage.saveTracks(mapToTrackDtoList(saveTracks))
    }

    override fun getTracks(): List<Track> {
        return mapToTrackList(localStorage.getTracks())
    }

    override fun clear() {
        localStorage.clear()
    }

    private fun mapToTrackDto(track: Track): TrackDto {
        return TrackDto(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    private fun mapToTrack(track: TrackDto): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    private fun mapToTrackDtoList(tracks: List<Track>): List<TrackDto> {
        return tracks.map { mapToTrackDto(it) }
    }

    private fun mapToTrackList(tracks: List<TrackDto>): List<Track> {
        return tracks.map { mapToTrack(it) }
    }
}