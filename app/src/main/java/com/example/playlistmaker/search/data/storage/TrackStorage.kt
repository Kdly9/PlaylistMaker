package com.example.playlistmaker.search.data.storage

import com.example.playlistmaker.search.data.dto.TrackDto

interface TrackStorage {
    fun saveTracks(tracks: List<TrackDto>)
    fun getTracks(): List<TrackDto>
    fun clear()
}