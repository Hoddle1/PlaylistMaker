package com.example.playlistmaker.presentation.ui.search.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.theme.Fonts

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onClear: () -> Unit,
) {
    BasicTextField(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                colorResource(R.color.search_field_background)
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .onFocusChanged {
                if (it.isFocused) {
                    onFocus()
                }
            },
        value = value,
        onValueChange = { text ->
            onValueChange(text)
        },
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontFamily = Fonts.YSDisplay,
            fontWeight = FontWeight.Normal,
            color = colorResource(R.color.main_text)
        ),
        cursorBrush = SolidColor(
            colorResource(R.color.control)
        ),
        decorationBox = { innerTextField ->
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search_16),
                    contentDescription = null,
                    tint = colorResource(R.color.text_color_hint)
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.weight(1f)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            stringResource(R.string.search),
                            color = colorResource(R.color.text_color_hint)
                        )
                    }
                    innerTextField()
                }
                Spacer(Modifier.width(8.dp))

                if (value.isNotEmpty()) {
                    Icon(
                        modifier = Modifier
                            .clickable(
                                onClick = onClear
                            ),
                        painter = painterResource(R.drawable.ic_cross),
                        contentDescription = null,
                        tint = colorResource(R.color.text_color_hint),
                    )
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchFieldLightPreview() {
    SearchField(
        value = "",
        onValueChange = {},
        onFocus = {},
        onClear = {}
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SearchFieldLightDarkPreview() {
    SearchField(
        value = "dasdasd",
        onValueChange = {},
        onFocus = {},
        onClear = {}
    )
}
