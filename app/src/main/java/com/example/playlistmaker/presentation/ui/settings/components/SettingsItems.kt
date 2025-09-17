package com.example.playlistmaker.presentation.ui.settings.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.playlistmaker.R

@Composable
fun SettingsItems(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onShareApp: () -> Unit,
    onSendSupport: () -> Unit,
    onShowUserAgreement: () -> Unit,
) {
    LazyColumn {
        item {
            SettingsSwitchItem(
                modifier = Modifier,
                text = stringResource(R.string.night_theme),
                checked = checked,
                onCheckedChange = { checked ->
                    onCheckedChange(checked)
                }
            )
            SettingsButtonItem(
                modifier = Modifier,
                text = stringResource(R.string.share_app),
                iconId = R.drawable.ic_share,
                onClick = onShareApp
            )
            SettingsButtonItem(
                modifier = Modifier,
                text = stringResource(R.string.write_to_support),
                iconId = R.drawable.ic_support,
                onClick = onSendSupport
            )
            SettingsButtonItem(
                modifier = Modifier,
                text = stringResource(R.string.agreement),
                iconId = R.drawable.ic_arrow_forward,
                onClick = onShowUserAgreement
            )
        }
    }
}