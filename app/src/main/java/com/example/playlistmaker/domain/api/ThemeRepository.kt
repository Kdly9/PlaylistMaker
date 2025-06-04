package com.example.playlistmaker.domain.api

interface ThemeRepository {
    fun isDarkThemeSelected(): Boolean
    fun setDarkTheme(active: Boolean)
    fun savedParametersExist(): Boolean
}