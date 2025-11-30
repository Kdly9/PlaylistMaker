package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val playlistName: String,
    val playlistDescription: String? = null,
    val imagePath: String? = null,
    val tracksIds: String?,
    val tracksCount: Int = 0,
    val addedDate: Long = System.currentTimeMillis()
)