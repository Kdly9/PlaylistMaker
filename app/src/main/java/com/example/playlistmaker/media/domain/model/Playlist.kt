package com.example.playlistmaker.media.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val trackIds: List<String>,
    val tracksCount: Int,
    val addedDate: Long = System.currentTimeMillis()
)