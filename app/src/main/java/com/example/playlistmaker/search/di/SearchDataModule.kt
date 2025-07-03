package com.example.playlistmaker.search.di

import android.content.Context
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ItunesApiNetworkClient
import com.example.playlistmaker.search.data.network.itunesApi
import com.example.playlistmaker.search.data.storage.SharedPrefsTrackStorage
import com.example.playlistmaker.search.data.storage.TrackStorage
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchDataModule = module {
    single<itunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(itunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("tracks_history", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<NetworkClient> {
        ItunesApiNetworkClient(get())
    }

    single<TrackStorage> {
        SharedPrefsTrackStorage(get(), get())
    }

}