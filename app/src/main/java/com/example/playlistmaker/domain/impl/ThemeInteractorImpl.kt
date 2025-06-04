package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository

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