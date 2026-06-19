package com.example.playlistmaker.media.di

import androidx.room.Room
import com.example.playlistmaker.media.data.converter.PlaylistDbConvertor
import com.example.playlistmaker.media.data.converter.PlaylistsTrackDbConverter
import com.example.playlistmaker.media.data.converter.TrackDbConverter
import com.example.playlistmaker.media.data.db.AppDataBase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDataBase::class.java, "database.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }
    factory { TrackDbConverter() }

    factory { PlaylistsTrackDbConverter() }

    factory { PlaylistDbConvertor() }

}