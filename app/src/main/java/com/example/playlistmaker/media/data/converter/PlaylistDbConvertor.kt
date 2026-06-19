package com.example.playlistmaker.media.data.converter

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.imagePath,
            playlist.trackIds.joinToString(","),
            playlist.tracksCount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imagePath,
            playlist.tracksIds?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
            playlist.tracksCount
        )
    }
}