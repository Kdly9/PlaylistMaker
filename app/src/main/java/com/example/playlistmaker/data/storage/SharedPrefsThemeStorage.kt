package com.example.playlistmaker.data.storage

import android.content.Context
import androidx.core.content.edit

const val SETTINGS_PREFERENCES = "playlist_maker_prefs"
const val DARK_THEME_KEY = "dark_theme_enabled"

class SharedPrefsThemeStorage(context: Context) : ThemeStorage {
    private val sharedPreferences =
        context.getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE)

    override fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(com.example.playlistmaker.ui.DARK_THEME_KEY, enabled)
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