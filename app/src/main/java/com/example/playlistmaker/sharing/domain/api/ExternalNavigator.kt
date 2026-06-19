package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.domain.models.MailData

interface ExternalNavigator {
    fun shareLink(link: String, label: String)
    fun openLink(link: String)
    fun openEmail(data: MailData)
}