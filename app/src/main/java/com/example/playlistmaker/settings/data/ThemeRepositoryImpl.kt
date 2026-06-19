package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.data.storage.ThemeStorage
import com.example.playlistmaker.settings.domain.api.ThemeRepository

class ThemeRepositoryImpl(private val localStorage: ThemeStorage): ThemeRepository {
    override fun isDarkThemeSelected(): Boolean {
        return localStorage.getDarkThemeEnabled()
    }

    override fun setDarkTheme(active: Boolean) {
        localStorage.setDarkTheme(active)
    }

    override fun savedParametersExist(): Boolean {
        return localStorage.containDarkModeKey()
    }
}