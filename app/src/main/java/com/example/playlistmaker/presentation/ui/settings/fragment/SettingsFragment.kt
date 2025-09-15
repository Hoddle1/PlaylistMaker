package com.example.playlistmaker.presentation.ui.settings.fragment

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.settings.components.SettingsButtonItem
import com.example.playlistmaker.presentation.ui.settings.components.SettingsSwitchItem
import com.example.playlistmaker.presentation.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.presentation.ui.theme.Fonts
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

                val darkTheme by viewModel.observeDarkTheme().observeAsState()

                val checked = when (darkTheme) {
                    false -> {
                        false
                    }

                    null -> {
                        val currentNightMode =
                            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                        currentNightMode == Configuration.UI_MODE_NIGHT_YES
                    }

                    true -> {
                        true
                    }
                }

                Scaffold(
                  backgroundColor = Color(LocalContext.current.getColor(R.color.default_background))
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {
                        Header()
                        SettingsItems(
                            checked = checked
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun Header() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)

        ) {
            Text(
                text = LocalContext.current.getString(R.string.settings),
                color = Color(LocalContext.current.getColor(R.color.settings_text_button)),
                fontWeight = FontWeight.W500,
                fontFamily = Fonts.YSDisplay,
                fontSize = 22.sp
            )
        }
    }


    @Composable
    fun SettingsItems(
        checked: Boolean
    ) {
        LazyColumn{
            item {
                SettingsSwitchItem(
                    modifier = Modifier,
                    text = LocalContext.current.getString(R.string.night_theme),
                    checked = checked,
                    onCheckedChange = { checked ->
                        viewModel.switchTheme(checked)
                    }
                )
                SettingsButtonItem(
                    modifier = Modifier,
                    text = LocalContext.current.getString(R.string.share_app),
                    iconId = R.drawable.ic_share,
                    onClick = { shareApp() }
                )
                SettingsButtonItem(
                    modifier = Modifier,
                    text = LocalContext.current.getString(R.string.write_to_support),
                    iconId = R.drawable.ic_support,
                    onClick = { sendSupport() }
                )
                SettingsButtonItem(
                    modifier = Modifier,
                    text = LocalContext.current.getString(R.string.agreement),
                    iconId = R.drawable.ic_arrow_forward,
                    onClick = { showUserAgreement() }
                )
            }
        }
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