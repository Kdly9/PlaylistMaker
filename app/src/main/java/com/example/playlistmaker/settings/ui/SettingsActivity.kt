package com.example.playlistmaker.settings.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel by viewModel<SettingsViewModel>() { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.toolbarBack.setNavigationOnClickListener {
            finish()
        }

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