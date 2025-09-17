package com.example.playlistmaker.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.theme.Fonts

@Composable
fun TrackItem(
    modifier: Modifier = Modifier,
    track: Track,
    onClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)

    ) {
        AsyncImage(
            modifier = Modifier
                .size(45.dp)
                .clip(
                    RoundedCornerShape(2.dp)
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .data(track.artworkUrl100)
                .crossfade(false) // меньше джанка на скролле
                .build(),
            placeholder = painterResource(R.drawable.track_image_placeholder),
            error = painterResource(R.drawable.track_image_placeholder),
            fallback = painterResource(R.drawable.track_image_placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = track.trackName,
                fontFamily = Fonts.YSDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colorResource(R.color.settings_text_button)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    modifier = Modifier
                        .weight(1F, fill = false),
                    text = track.artistName,
                    fontFamily = Fonts.YSDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colorResource(R.color.track_artist_text)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_dot),
                    contentDescription = null,
                    tint = colorResource(R.color.track_artist_text)
                )
                Text(
                    text = track.trackTimeMillis,
                    fontFamily = Fonts.YSDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colorResource(R.color.track_artist_text)
                )

            }

        }

        Spacer(Modifier.width(8.dp))

        Image(
            painter = painterResource(R.drawable.ic_arrow_forward),
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackItemPreview() {
    TrackItem(
        track = Track(
            trackId = 123,
            trackName = stringResource(R.string.track_name),
            artistName = stringResource(R.string.track_author),
            trackTimeMillis = stringResource(R.string.track_timing),
            artworkUrl100 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTnkXX1msb3FcwUKdveOb4VJ_8dlsezqUlqEQ&s",
            primaryGenreName = "asdasdasd",
            country = "asdasdasd",
            previewUrl = "asdasdasd",
            isFavorite = true,
            collectionName = null,
            releaseDate = null
        ),
        onClick = {}
    )
}