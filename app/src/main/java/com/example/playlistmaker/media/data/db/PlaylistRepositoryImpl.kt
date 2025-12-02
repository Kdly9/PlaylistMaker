package com.example.playlistmaker.media.data.db

import com.example.playlistmaker.media.data.converter.PlaylistDbConvertor
import com.example.playlistmaker.media.data.converter.PlaylistsTrackDbConverter
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val playlistsTrackDbConverter: PlaylistsTrackDbConverter
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        appDataBase.playlistDao().insertPlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDataBase.playlistDao().update(playlistDbConvertor.map(playlist))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return appDataBase.playlistDao().getAllPlaylists().map { entities ->
            entities.map { playlistDbConvertor.map(it) }
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return appDataBase.playlistDao().getById(id)?.let { playlistDbConvertor.map(it) }
    }

    override suspend fun deletePlaylistById(id: Long) {
        appDataBase.playlistDao().delete(id)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return appDataBase.playlistDao().getAllPlaylistsSync().map { playlistDbConvertor.map(it) }
    }

    override suspend fun insertTrack(track: Track) {
        appDataBase.playlistsTracksDao().insert(playlistsTrackDbConverter.map(track))
    }

    override suspend fun removeTrackFromPlaylist(trackId: String, playlist: Playlist) {
        val updatedTrackIds = playlist.trackIds - trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )
        appDataBase.playlistDao().update(playlistDbConvertor.map(updatedPlaylist))

        checkAndRemoveOrphanedTrack(trackId)
    }

    override suspend fun checkAndRemoveOrphanedTrack(trackId: String) {
        val allPlaylists = appDataBase.playlistDao().getAllPlaylistsSync()

        val isTrackUsed = allPlaylists.any { playlist ->
            playlist.tracksIds?.contains(trackId) == true
        }

        if (!isTrackUsed) {
            appDataBase.playlistsTracksDao().deleteTrack(trackId)
        }
    }

    override fun getTracksByIds(trackIds: List<String>): Flow<List<Track>> {
        return appDataBase.playlistsTracksDao()
            .getTracksByIds(trackIds)
            .map { entities ->
                entities.map { playlistsTrackDbConverter.map(it) }
            }
    }
}