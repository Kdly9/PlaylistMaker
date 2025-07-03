package com.example.playlistmaker.sharing.domain.impl

import android.app.Activity
import com.example.playlistmaker.sharing.domain.StringProvider
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: (Activity) -> ExternalNavigator,
    private val stringProvider: StringProvider
) : SharingInteractor {
    override fun shareApp(activity: Activity) {
        externalNavigator(activity).shareLink(
            stringProvider.getShareLink(),
            stringProvider.getShareLinkLabel()
        )
    }

    override fun openTerms(activity: Activity) {
        externalNavigator(activity).openLink(stringProvider.getOpenLink())
    }

    override fun openSupport(activity: Activity) {
        externalNavigator(activity).openEmail(stringProvider.getMailData())
    }
}