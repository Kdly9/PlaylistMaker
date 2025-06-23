package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.api.ThemeRepository

class ThemeInteractorImpl(private val repository: ThemeRepository): ThemeInteractor {
    override fun checkParamsExisting(): Boolean {
        return repository.savedParametersExist()
    }

    override fun enableDarkMode(active: Boolean) {
        repository.setDarkTheme(active)
    }

    override fun isDarkMode(): Boolean {
        return repository.isDarkThemeSelected()
    }
}