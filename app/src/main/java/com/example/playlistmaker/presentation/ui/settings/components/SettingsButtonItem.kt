package com.example.playlistmaker.presentation.ui.settings.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.theme.Fonts


@Composable
fun SettingsButtonItem(
    modifier: Modifier,
    text: String,
    iconId: Int,
    contentDescription: String = "",
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)

            .clickable(
                onClick = {
                    onClick()
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            )
            .padding(horizontal = 16.dp)


    ) {
        Text(
            text = text,
            color = Color(LocalContext.current.getColor(R.color.settings_text_button)),
            fontWeight = FontWeight.Normal,
            fontFamily = Fonts.YSDisplay,
            fontSize = 16.sp
        )
        Icon(
            painter = painterResource(iconId),
            contentDescription = contentDescription,
            tint = Color(LocalContext.current.getColor(R.color.settings_icon_hint)),
        )
    }
}


@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun SettingsButtonItemLightPreview() {
    SettingsButtonItem(
        modifier = Modifier,
        text = LocalContext.current.getString(R.string.share_app),
        iconId = R.drawable.ic_share,
        onClick = {}
    )
}

@Preview(showBackground = true, showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SettingsButtonItemDarkPreview() {
    SettingsButtonItem(
        modifier = Modifier,
        text = LocalContext.current.getString(R.string.share_app),
        iconId = R.drawable.ic_share,
        onClick = {}
    )
}