package com.example.playlistmaker.media.data.converter

import com.example.playlistmaker.media.data.db.entity.PlaylistsTrack
import com.example.playlistmaker.search.domain.models.Track

class PlaylistsTrackDbConverter {
    fun map(track: Track): PlaylistsTrack {
        return PlaylistsTrack(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavourite = track.isFavorite
        )
    }

    fun map(track: PlaylistsTrack): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavourite
        )
    }
}