package com.example.playlistmaker.presentation.ui.library.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.presentation.ui.theme.Fonts


@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    playlist: Playlist,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
            )
    ) {
        AsyncImage(
            modifier = Modifier
                .size(160.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                ),
            model = playlist.coverImagePath,
            placeholder = painterResource(R.drawable.track_image_placeholder),
            error = painterResource(R.drawable.track_image_placeholder),
            fallback = painterResource(R.drawable.track_image_placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = playlist.name,
            color = colorResource(R.color.playlist_text_color),
            fontFamily = Fonts.YSDisplay,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = LocalResources.current.getQuantityString(
                R.plurals.tracks,
                playlist.tracksCount,
                playlist.tracksCount,
            ),
            color = colorResource(R.color.playlist_text_color),
            fontFamily = Fonts.YSDisplay,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}

@Preview
@Composable
fun PlaylistItemPreview() {
   PlaylistItem(
       playlist = Playlist(
           id = 123,
           name = "asdasdasd",
           coverImagePath = "asdasdasd",
           description = "deeqwedq",
           trackIds = emptyList(),
           tracksCount = 24
       ),
       onClick = {}
   )
}