package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.StringProvider
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val stringProvider: StringProvider
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(
            stringProvider.getShareLink(),
            stringProvider.getShareLinkLabel()
        )
    }

    override fun openTerms() {
        externalNavigator.openLink(stringProvider.getOpenLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(stringProvider.getMailData())
    }
}
