package com.example.playlistmaker.presentation.ui.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.components.RoundedButton
import com.example.playlistmaker.presentation.ui.theme.Fonts

@Composable
fun NoInternetView(
    onRetry: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(140.dp),
            painter = painterResource(R.drawable.no_internet),
            contentDescription = null
        )

        Spacer(Modifier.height(16.dp))

        Text(
            textAlign = TextAlign.Center,
            text = stringResource(R.string.no_internet),
            color = colorResource(R.color.default_placeholder_text),
            fontFamily = Fonts.YSDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(24.dp))

        RoundedButton(
            text = stringResource(R.string.update),
            onClick = onRetry
        )
    }
}