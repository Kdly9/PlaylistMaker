package com.example.playlistmaker.settings.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.root.ui.RootActivity
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel by viewModel<SettingsViewModel>() { parametersOf(this) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.observeMode().observe(viewLifecycleOwner) {
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
            (requireActivity() as RootActivity).switchTheme(checked)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}