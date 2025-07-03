package com.example.playlistmaker.settings.ui.view_model

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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

    companion object {
        fun getFactory(
            sharingInteractor: SharingInteractor,
            themeInteractor: ThemeInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    sharingInteractor,
                    themeInteractor
                )
            }
        }
    }

    fun shareApp(activity: Activity) {
        sharingInteractor.shareApp(activity)
    }

    fun openSupport(activity: Activity) {
        sharingInteractor.openSupport(activity)
    }

    fun openTerms(activity: Activity) {
        sharingInteractor.openTerms(activity)
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