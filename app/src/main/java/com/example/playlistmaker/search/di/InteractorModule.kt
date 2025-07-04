package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<TracksHistoryInteractor>{
        TracksHistoryInteractorImpl(get())
    }

    single<TracksInteractor>{
        TracksInteractorImpl(get())
    }


}