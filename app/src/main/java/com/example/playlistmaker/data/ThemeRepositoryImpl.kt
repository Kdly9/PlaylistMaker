package com.example.playlistmaker.data

import com.example.playlistmaker.data.storage.ThemeStorage
import com.example.playlistmaker.domain.api.ThemeRepository

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