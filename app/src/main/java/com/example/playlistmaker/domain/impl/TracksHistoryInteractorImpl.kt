package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.models.Track

class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository):TracksHistoryInteractor {
    override fun getHistory(): List<Track> {
        return repository.getTracks()
    }

    override fun saveHistory(tracks: List<Track>) {
        repository.saveTracks(tracks)
    }
}