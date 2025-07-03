package com.example.playlistmaker.sharing.domain.impl

import android.content.Context
import com.example.playlistmaker.sharing.domain.StringProvider
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: (Context) -> ExternalNavigator,
    private val stringProvider: StringProvider
) : SharingInteractor {
    override fun shareApp(context: Context) {
        externalNavigator(context).shareLink(
            stringProvider.getShareLink(),
            stringProvider.getShareLinkLabel()
        )
    }

    override fun openTerms(context: Context) {
        externalNavigator(context).openLink(stringProvider.getOpenLink())
    }

    override fun openSupport(context: Context) {
        externalNavigator(context).openEmail(stringProvider.getMailData())
    }
}