package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.player.data.MediaManagerImpl
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.ItunesApiNetworkClient
import com.example.playlistmaker.settings.data.storage.SharedPrefsThemeStorage
import com.example.playlistmaker.search.data.storage.SharedPrefsTrackStorage
import com.example.playlistmaker.player.domain.api.MediaInteractor
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.StringProviderImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl

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

    fun getMediaInteractor(): MediaInteractor {
        return MediaPlayerInteractorImpl(MediaManagerImpl())
    }

    fun getSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            ExternalNavigatorImpl(context), StringProviderImpl(
                context
            )
        )
    }
}