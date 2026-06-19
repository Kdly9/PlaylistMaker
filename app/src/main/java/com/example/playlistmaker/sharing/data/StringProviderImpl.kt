package com.example.playlistmaker.sharing.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.StringProvider
import com.example.playlistmaker.sharing.domain.models.MailData

class StringProviderImpl(private val context: Context) : StringProvider {
    override fun getShareLink(): String {
        return context.getString(R.string.practicum_url)
    }

    override fun getShareLinkLabel(): String {
        return context.getString(R.string.share_label)
    }

    override fun getOpenLink(): String {
        return context.getString(R.string.agreement_url)
    }

    override fun getMailData(): MailData {
        return MailData(
            context.getString(R.string.mail_topic),
            context.getString(R.string.mail_text),
            context.getString(R.string.email)
        )
    }
}