package com.example.playlistmaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.theme.Fonts

@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = { onClick() },
        shape = RoundedCornerShape(54.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(R.color.default_btn_background)
        ),
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 14.dp)
    ) {
        Text(
            text = text,
            color = colorResource(R.color.default_btn_text),
            fontFamily = Fonts.YSDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 0.sp
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun RoundButtonLightPreview() {
    RoundedButton(
        text = "Обновить",
        onClick = {}
    )
}

@Preview(
    showSystemUi = false, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun RoundButtonDarkPreview() {
    RoundedButton(
        text = "Обновить",
        onClick = {}
    )
}