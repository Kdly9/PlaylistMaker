package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun update(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table ORDER BY playlistName ASC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table WHERE playlistId = :id")
    suspend fun getById(id: Long): PlaylistEntity?

    @Query("DELETE FROM playlist_table WHERE playlistId = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylistsSync(): List<PlaylistEntity>


}