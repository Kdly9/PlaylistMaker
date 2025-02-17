package com.example.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial

const val SETTINGS_PREFERENCES = "playlist_maker_prefs"
const val DARK_THEME_KEY = "dark_theme_enabled"

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs = getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE)

        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Toolbar>(R.id.toolbarBack)
        backButton.setNavigationOnClickListener {
            finish()
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
            showActivityOrToast(chooser)
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
            showActivityOrToast(supportIntent)
        }

        val agreementButton = findViewById<TextView>(R.id.userAgreement)
        agreementButton.setOnClickListener {
            val showIntent = Intent(Intent.ACTION_VIEW)
            showIntent.data = Uri.parse(getString(R.string.agreement_url))
            showActivityOrToast(showIntent)
        }

        val switchTheme = findViewById<SwitchMaterial>(R.id.themeSwitch)
        if (sharedPrefs.contains(DARK_THEME_KEY)) {
            switchTheme.isChecked = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
        } else {
            val currentNightMode =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    switchTheme.isChecked = false
                }

                Configuration.UI_MODE_NIGHT_YES -> {
                    switchTheme.isChecked = true
                }
            }
        }

        switchTheme.setOnCheckedChangeListener { _, checked ->
            sharedPrefs.edit()
                .putBoolean(DARK_THEME_KEY, checked)
                .apply()
            (application as App).switchTheme(checked)
        }
    }

    private fun showActivityOrToast(intent: Intent) {
        try {
            startActivity(intent)
        } catch (ex: RuntimeException) {
            Toast.makeText(
                this,
                getString(R.string.no_apps_available_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}