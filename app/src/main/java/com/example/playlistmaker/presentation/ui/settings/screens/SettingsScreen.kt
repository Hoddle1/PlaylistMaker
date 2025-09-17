package com.example.playlistmaker.presentation.ui.settings.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.components.Header
import com.example.playlistmaker.presentation.ui.settings.components.SettingsItems
import com.example.playlistmaker.presentation.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onShareApp: () -> Unit,
    onSendSupport: () -> Unit,
    onShowUserAgreement: () -> Unit,
) {
    val darkTheme by viewModel.observeDarkTheme().observeAsState()

    val checked = when (darkTheme) {
        false -> {
            false
        }

        null -> {
            val currentNightMode =
                LocalConfiguration.current.uiMode and Configuration.UI_MODE_NIGHT_MASK
            currentNightMode == Configuration.UI_MODE_NIGHT_YES
        }

        true -> {
            true
        }
    }

    Scaffold(
        backgroundColor = colorResource(R.color.default_background)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Header(
                text = stringResource(R.string.settings)
            )
            SettingsItems(
                checked = checked,
                onCheckedChange = { checked ->
                    viewModel.switchTheme(checked)
                },
                onSendSupport = onShareApp,
                onShareApp = onSendSupport,
                onShowUserAgreement = onShowUserAgreement
            )
        }
    }
}