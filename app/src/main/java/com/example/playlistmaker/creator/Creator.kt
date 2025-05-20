package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.ItunesApiNetworkClient
import com.example.playlistmaker.data.storage.SharedPrefsThemeStorage
import com.example.playlistmaker.data.storage.SharedPrefsTrackStorage
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TracksHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private lateinit var appContext: Context

    fun initContext(context: Context) {
        appContext = context.applicationContext
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(ItunesApiNetworkClient())
    }

    fun provideTrackInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getTracksHistoryRepository(): TracksHistoryRepository {
        require(::appContext.isInitialized) { "context is not initialized!" }
        return TracksHistoryRepositoryImpl(SharedPrefsTrackStorage(appContext))
    }

    fun getTracksHistoryRepositoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository())
    }

    private fun getThemeRepository(): ThemeRepository {
        require(::appContext.isInitialized) { "context is not initialized!" }
        return ThemeRepositoryImpl(SharedPrefsThemeStorage(appContext))
    }

    fun getThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }
}