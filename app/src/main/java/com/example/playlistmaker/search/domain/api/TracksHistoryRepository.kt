package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksHistoryRepository {

    fun saveTracks(saveTracks: List<Track>)
    fun getTracks(): List<Track>
    fun clear()
}