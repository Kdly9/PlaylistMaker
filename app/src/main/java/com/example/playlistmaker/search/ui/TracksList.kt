package com.example.playlistmaker.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchFragment.TimeFormatter
import com.example.playlistmaker.theme.LocalCustomColors
import com.example.playlistmaker.theme.LocalTypography

@Composable
fun TracksList(
    tracks: List<Track>,
    modifier: Modifier = Modifier,
    onTrackClick: ((Track) -> Unit)?
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(tracks) { track ->
            TrackItem(
                track = track,
                onClick = { onTrackClick?.let { it(track) } }
            )
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = track.artworkUrl100,
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(2.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.mock_image),
                placeholder = painterResource(R.drawable.mock_image)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                // Название трека
                Text(
                    text = track.trackName,
                    style = LocalTypography.current.regular16,
                    color = LocalCustomColors.current.textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Вторая строка с исполнителем и длительностью
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Исполнитель
                    Text(
                        text = track.artistName,
                        style = LocalTypography.current.regular11,
                        color = LocalCustomColors.current.primaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Разделитель
                    Icon(
                        painter = painterResource(R.drawable.ic_point),
                        contentDescription = null,
                        tint = LocalCustomColors.current.primaryColor,
                        modifier = Modifier
                            .wrapContentWidth()
                    )

                    // Длительность трека
                    Text(
                        text = TimeFormatter.formatTime(track.trackTimeMillis.toLong()),
                        style = LocalTypography.current.regular11,
                        color = LocalCustomColors.current.primaryColor,
                        maxLines = 1
                    )

                }
            }
            // Иконка вперед
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .wrapContentSize(),
                painter = painterResource(R.drawable.ic_arrow_forvard),
                contentDescription = null,
                tint = LocalCustomColors.current.primaryColor,
            )

        }
    }
}