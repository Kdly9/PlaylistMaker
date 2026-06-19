package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.ui.ThemeState
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor,
) : ViewModel() {


    private val _themeState = MutableStateFlow<ThemeState>(ThemeState.NoSavedParams)
    val themeState: StateFlow<ThemeState> = _themeState.asStateFlow()

    init {
        getTheme()
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun isDarkModeTheme() {
        getTheme()
    }

    private fun getTheme() {
        viewModelScope.launch {
            if (themeInteractor.checkParamsExisting()) {
                val darkMode = themeInteractor.isDarkMode()
                _themeState.value = ThemeState.SavedParamsExist(darkMode)
            } else {
                _themeState.value = ThemeState.NoSavedParams
            }
        }
    }

    fun enableDarkMode(enable: Boolean) {
        viewModelScope.launch {
            themeInteractor.enableDarkMode(enable)
            _themeState.value = ThemeState.SavedParamsExist(enable)
        }
    }
}