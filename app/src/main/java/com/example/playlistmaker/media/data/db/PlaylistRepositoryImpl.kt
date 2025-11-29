package com.example.playlistmaker.media.data.db

import com.example.playlistmaker.media.data.converter.PlaylistDbConvertor
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val playlistDbConvertor: PlaylistDbConvertor
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

    override suspend fun insertTrack(track: Track) {
         /*appDataBase.playlistTracksDao().insert(track.toPlaylistTrackEntity())*/
     }

    override suspend fun getPlaylists(): List<Playlist> {
        return appDataBase.playlistDao().getAllPlaylistsSync().map { playlistDbConvertor.map(it) }
    }
}