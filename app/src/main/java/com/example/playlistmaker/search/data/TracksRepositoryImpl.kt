package com.example.playlistmaker.search.data

import com.example.playlistmaker.media.data.db.AppDataBase
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient, private val appDatabase: AppDataBase) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                val data = (response as TrackResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTime,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                }
                emit(Resource.Success(data))
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }

}
