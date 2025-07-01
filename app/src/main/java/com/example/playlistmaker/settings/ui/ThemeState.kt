package com.example.playlistmaker.settings.ui

sealed class ThemeState {
    data class SavedParamsExist(val isDarkMode: Boolean) : ThemeState()
    object NoSavedParams : ThemeState()
}