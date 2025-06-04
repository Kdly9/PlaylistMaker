package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return if (response.resultCode == 200) {
            (response as TrackResponse).results.map {
                Track(it.trackId, it.trackName, it.artistName, it.trackTime, it.artworkUrl100, it.collectionName, it.releaseDate, it.primaryGenreName, it.country, it.previewUrl) }
        } else {
            emptyList()
        }
    }
}