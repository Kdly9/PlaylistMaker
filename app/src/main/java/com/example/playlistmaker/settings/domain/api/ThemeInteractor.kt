package com.example.playlistmaker.settings.domain.api

interface ThemeInteractor {
    fun checkParamsExisting(): Boolean
    fun enableDarkMode(active: Boolean)
    fun isDarkMode(): Boolean
}