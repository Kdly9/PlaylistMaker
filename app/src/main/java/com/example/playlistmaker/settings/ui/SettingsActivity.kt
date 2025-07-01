package com.example.playlistmaker.settings.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.toolbarBack.setNavigationOnClickListener {
            finish()
        }

        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory(
                Creator.getSharingInteractor(this),
                Creator.getThemeInteractor()
            )
        )[SettingsViewModel::class.java]

        settingsViewModel.observeMode().observe(this) {
            when (it) {
                ThemeState.NoSavedParams -> {
                    val currentNightMode =
                        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

                    when (currentNightMode) {
                        Configuration.UI_MODE_NIGHT_NO -> {
                            binding.themeSwitch.isChecked = false
                        }

                        Configuration.UI_MODE_NIGHT_YES -> {
                            binding.themeSwitch.isChecked = true
                        }
                    }
                }

                is ThemeState.SavedParamsExist -> binding.themeSwitch.isChecked = it.isDarkMode

            }
        }

        binding.shareButton.setOnClickListener {
            settingsViewModel.shareApp()
        }
        binding.supportButton.setOnClickListener {
            settingsViewModel.openSupport()
        }

        binding.userAgreement.setOnClickListener {
            settingsViewModel.openTerms()
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.enableDarkMode(checked)
            (application as App).switchTheme(checked)
        }
    }
}