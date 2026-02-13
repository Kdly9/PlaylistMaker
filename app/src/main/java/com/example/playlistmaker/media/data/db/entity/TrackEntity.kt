package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "track_table")
data class TrackEntity (
    @PrimaryKey
    val trackId: String,
    var trackName: String,
    var artistName: String,
    var trackTimeMillis: String,
    var artworkUrl100: String?,
    var collectionName: String?,
    var releaseDate: String?,
    var primaryGenreName: String,
    var country: String,
    var previewUrl: String,
    var isFavorite: Boolean = false,
    val addedDate: Long = System.currentTimeMillis()
)