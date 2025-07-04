package com.example.playlistmaker.sharing.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.MailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(link: String, label: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            link
        )

        val chooser = Intent.createChooser(shareIntent, label).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(chooser)
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
        } catch (ex: RuntimeException) {
            ex.printStackTrace()
        }
    }


    override fun openLink(link: String) {
        val showIntent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        showIntent.data = Uri.parse(link)
        context.startActivity(showIntent)
    }

    override fun openEmail(data: MailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(data.email))
        supportIntent.putExtra(
            Intent.EXTRA_TEXT, data.text
        )
        supportIntent.putExtra(
            Intent.EXTRA_SUBJECT, data.topic
        )
        context.startActivity(supportIntent)
    }
}