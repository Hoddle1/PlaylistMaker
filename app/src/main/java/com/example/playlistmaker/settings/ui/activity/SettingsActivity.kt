package com.example.playlistmaker.settings.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel by lazy {
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(binding.llMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.iBtnBack.setOnClickListener { finish() }

        viewModel.observeDarkTheme().observe(this) { isDarkMode ->
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