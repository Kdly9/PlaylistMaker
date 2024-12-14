package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Toolbar>(R.id.toolbarBack)
        backButton.setNavigationOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }
        val shareButton = findViewById<TextView>(R.id.shareButton)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.practicum_url)
            )
            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_label))
            if (shareIntent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.no_apps_available_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val supportButton = findViewById<TextView>(R.id.supportButton)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
            supportIntent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.mail_text)
            )
            supportIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.mail_topic)
            )
            try {
                startActivity(supportIntent)
            } catch (ex: RuntimeException) {
                Toast.makeText(
                    this,
                    getString(R.string.no_apps_available_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val agreementButton = findViewById<TextView>(R.id.userAgreement)
        agreementButton.setOnClickListener {
            val showIntent = Intent(Intent.ACTION_VIEW)
            showIntent.data = Uri.parse(getString(R.string.agreement_url))
            try {
                startActivity(showIntent)
            } catch (ex: RuntimeException) {
                Toast.makeText(
                    this,
                    getString(R.string.no_apps_available_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}