package com.example.playlistmaker.presentation.ui.settings.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.settings.screens.SettingsScreen


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

                SettingsScreen(
                    onSendSupport = { sendSupport() },
                    onShareApp = { shareApp() },
                    onShowUserAgreement = { showUserAgreement() }
                )
            }
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_link))
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
    }

    private fun sendSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = "mailto:".toUri()
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_email_to)))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
        startActivity(emailIntent)
    }

    private fun showUserAgreement() {
        val url = getString(R.string.user_agreement_link).toUri()
        val agreementIntent = Intent(Intent.ACTION_VIEW, url)
        startActivity(agreementIntent)
    }
}