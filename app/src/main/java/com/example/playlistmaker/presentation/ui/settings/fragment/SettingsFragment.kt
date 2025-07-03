package com.example.playlistmaker.presentation.ui.settings.fragment

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.presentation.ui.settings.view_model.SettingsViewModel

import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeDarkTheme().observe(viewLifecycleOwner) { isDarkMode ->
            when (isDarkMode) {
                false -> {
                    binding.scThemeSwitcher.isChecked = false
                }

                null -> {
                    val currentNightMode =
                        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                    binding.scThemeSwitcher.isChecked =
                        currentNightMode == Configuration.UI_MODE_NIGHT_YES
                }

                true -> {
                    binding.scThemeSwitcher.isChecked = true
                }
            }
        }


        binding.scThemeSwitcher.apply {
            isSaveEnabled = false
            setOnCheckedChangeListener { _, checked -> viewModel.switchTheme(checked) }
        }

        binding.mbShareApp.setOnClickListener { shareApp() }

        binding.mbSendSupport.setOnClickListener { sendSupport() }

        binding.mbUserAgreement.setOnClickListener { showUserAgreement() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_link))
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
    }

    private fun sendSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.setData(Uri.parse("mailto:"))
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_email_to)))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
        startActivity(emailIntent)
    }

    private fun showUserAgreement() {
        val url = Uri.parse(getString(R.string.user_agreement_link))
        val agreementIntent = Intent(Intent.ACTION_VIEW, url)
        startActivity(agreementIntent)
    }
}