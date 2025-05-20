package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksHistoryRepository {

    fun saveTracks(saveTracks: List<Track>)
    fun getTracks(): List<Track>
    fun clear()
}