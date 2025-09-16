package com.example.playlistmaker.presentation.ui.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.theme.Fonts

@Composable
fun TrackItem(
    modifier: Modifier = Modifier,
    track: Track
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        AsyncImage(
            modifier = Modifier
                .size(45.dp)
                .clip(
                    RoundedCornerShape(2.dp)
                ),
            model = track.artworkUrl100,
            placeholder = painterResource(R.drawable.track_image_placeholder),
            contentDescription = "",
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
                color = Color(LocalContext.current.getColor(R.color.settings_text_button))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .weight(1F, fill = false),
                    text = track.artistName,
                    fontFamily = Fonts.YSDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(LocalContext.current.getColor(R.color.track_artist_text))
                )
                Icon(
                    painter = painterResource(R.drawable.ic_dot),
                    contentDescription = null,
                    tint = Color(LocalContext.current.getColor(R.color.track_artist_text))
                )
                Text(
                    text = track.trackTimeMillis,
                    fontFamily = Fonts.YSDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(LocalContext.current.getColor(R.color.track_artist_text))
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
            trackName = LocalContext.current.getString(R.string.track_name),
            artistName = LocalContext.current.getString(R.string.track_author),
            trackTimeMillis = LocalContext.current.getString(R.string.track_timing),
            artworkUrl100 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTnkXX1msb3FcwUKdveOb4VJ_8dlsezqUlqEQ&s",
            primaryGenreName = "asdasdasd",
            country = "asdasdasd",
            previewUrl = "asdasdasd",
            isFavorite = true,
            collectionName = null,
            releaseDate = null
        )
    )
}