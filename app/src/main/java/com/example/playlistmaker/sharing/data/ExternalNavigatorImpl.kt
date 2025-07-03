package com.example.playlistmaker.sharing.data

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.MailData

class ExternalNavigatorImpl(private val activity: Activity) : ExternalNavigator {
    override fun shareLink(link: String, label: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            link
        )
        val chooser = Intent.createChooser(shareIntent, label)
        try {
            activity.startActivity(chooser)
        } catch (_: RuntimeException) {

        }
    }

    override fun openLink(link: String) {
        val showIntent = Intent(Intent.ACTION_VIEW)
        showIntent.data = Uri.parse(link)
        activity.startActivity(showIntent)
    }

    override fun openEmail(data: MailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(data.email))
        supportIntent.putExtra(
            Intent.EXTRA_TEXT,
            data.text
        )
        supportIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            data.topic
        )
        activity.startActivity(supportIntent)
    }
}