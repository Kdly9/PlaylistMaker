package com.example.playlistmaker.settings.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.ui.ThemeState
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor,
) : ViewModel() {


    private val themeState = MutableLiveData<ThemeState>()
    fun observeMode(): LiveData<ThemeState> = themeState

    init {
        getTheme()
    }

    fun shareApp(context: Context) {
        sharingInteractor.shareApp(context)
    }

    fun openSupport(context: Context) {
        sharingInteractor.openSupport(context)
    }

    fun openTerms(context: Context) {
        sharingInteractor.openTerms(context)
    }

    fun isDarkModeTheme() {
        getTheme()
    }

    private fun getTheme() {
        if (themeInteractor.checkParamsExisting()) {
            val darkMode = themeInteractor.isDarkMode()
            themeState.postValue(ThemeState.SavedParamsExist(darkMode))
        } else {
            themeState.postValue(ThemeState.NoSavedParams)
        }
    }

    fun enableDarkMode(enable: Boolean) {
        themeInteractor.enableDarkMode(enable)
        themeState.postValue(ThemeState.SavedParamsExist(enable))
    }
}