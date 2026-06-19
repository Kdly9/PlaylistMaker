package com.example.playlistmaker.settings.domain.api

interface ThemeRepository {
    fun isDarkThemeSelected(): Boolean
    fun setDarkTheme(active: Boolean)
    fun savedParametersExist(): Boolean
}