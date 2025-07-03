package com.example.playlistmaker.sharing.domain.api

import android.app.Activity

interface SharingInteractor {
    fun shareApp(activity: Activity)
    fun openTerms(activity: Activity)
    fun openSupport(activity: Activity)
}