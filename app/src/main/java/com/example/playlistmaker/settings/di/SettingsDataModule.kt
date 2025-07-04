package com.example.playlistmaker.settings.di

import android.content.Context
import com.example.playlistmaker.settings.data.storage.SharedPrefsThemeStorage
import com.example.playlistmaker.settings.data.storage.ThemeStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val settingsDataModule = module {

    single {
        androidContext()
            .getSharedPreferences("playlist_maker_prefs", Context.MODE_PRIVATE)
    }


    single<ThemeStorage> {
        SharedPrefsThemeStorage(get())
    }
}