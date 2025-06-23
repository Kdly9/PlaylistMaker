package com.example.playlistmaker.settings.data.storage

interface ThemeStorage {
    fun setDarkTheme(enabled: Boolean)
    fun getDarkThemeEnabled(): Boolean
    fun containDarkModeKey(): Boolean
}