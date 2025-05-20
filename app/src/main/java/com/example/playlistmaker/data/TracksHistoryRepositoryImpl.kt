package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.storage.TrackStorage
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.models.Track

class TracksHistoryRepositoryImpl(private val localStorage: TrackStorage) :
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
            track.trackTime,
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