package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.ItunesApiNetworkClient
import com.example.playlistmaker.data.storage.SharedPrefsTrackStorage
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TracksHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(ItunesApiNetworkClient())
    }

    fun provideTrackInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun getTracksHistoryRepository(context: Context): TracksHistoryRepository{
        return TracksHistoryRepositoryImpl(SharedPrefsTrackStorage(context))
    }

    fun getTracksHistoryRepositoryInteractor(tracksHistoryRepository:  TracksHistoryRepository): TracksHistoryInteractorImpl {
        return TracksHistoryInteractorImpl(tracksHistoryRepository)
    }
}