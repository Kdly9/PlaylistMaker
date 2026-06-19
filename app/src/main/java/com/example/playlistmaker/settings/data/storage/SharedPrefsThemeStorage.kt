package com.example.playlistmaker.settings.data.storage

import android.content.SharedPreferences
import androidx.core.content.edit


const val DARK_THEME_KEY = "dark_theme_enabled"

class SharedPrefsThemeStorage(private val sharedPreferences: SharedPreferences) : ThemeStorage {

    override fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(DARK_THEME_KEY, enabled)
        }
    }

    override fun getDarkThemeEnabled(): Boolean {
        require(containDarkModeKey()) { "there is no dark_theme_enabled key!" }
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun containDarkModeKey(): Boolean {
        return sharedPreferences.contains(DARK_THEME_KEY)
    }

}