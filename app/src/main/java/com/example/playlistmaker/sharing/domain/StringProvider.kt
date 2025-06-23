package com.example.playlistmaker.sharing.domain

import com.example.playlistmaker.sharing.domain.models.MailData

interface StringProvider {
    fun getShareLink(): String
    fun getShareLinkLabel(): String
    fun getOpenLink(): String
    fun getMailData(): MailData
}