package com.example.playlistmaker.data.storage

import com.example.playlistmaker.data.dto.TrackDto

interface TrackStorage {
    fun saveTracks(tracks: List<TrackDto>)
    fun getTracks(): List<TrackDto>
    fun clear()
}