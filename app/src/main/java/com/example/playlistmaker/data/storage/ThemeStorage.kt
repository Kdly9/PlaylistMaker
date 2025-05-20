package com.example.playlistmaker.data.storage

interface ThemeStorage {
    fun setDarkTheme(enabled: Boolean)
    fun getDarkThemeEnabled(): Boolean
    fun containDarkModeKey(): Boolean
}