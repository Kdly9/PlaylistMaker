package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.PlaylistsTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTracksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(track: PlaylistsTrack)

    @Query("SELECT * FROM playlists_track_table WHERE trackId IN (:trackIds) ORDER BY addedDate DESC")
    fun getTracksByIds(trackIds: List<String>): Flow<List<PlaylistsTrack>>

    @Query("DELETE FROM playlists_track_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: String)
}