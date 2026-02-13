package com.example.playlistmaker.media.ui.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.view_model.PlaylistsViewModel
import com.example.playlistmaker.theme.LocalCustomColors
import com.example.playlistmaker.theme.LocalTypography

@Composable
fun MediaPlaylists(
    viewModel: PlaylistsViewModel, onPlaylistClick: (Playlist) -> Unit, newPlaylistClick: () -> Unit
) {

    val playlists = viewModel.observePlaylists.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddButton(newPlaylistClick)
        if (playlists.isEmpty()) {
            EmptyPlaylistsState()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
            ) {
                items(playlists.size) { index ->
                    PlaylistItem(
                        playlist = playlists[index],
                        onClick = { onPlaylistClick(playlists[index]) })
                }
            }
        }
    }
}

@Composable
fun AddButton(newPlaylistClick: () -> Unit) {
    Button(
        onClick = { newPlaylistClick() },
        modifier = Modifier
            .padding(top = 24.dp, bottom = 16.dp)
            .wrapContentWidth(),
        colors = androidx.compose.material.ButtonDefaults.buttonColors(
            backgroundColor = LocalCustomColors.current.historyTitleColor
        ),
        shape = RoundedCornerShape(50.dp)
    ) {
        androidx.compose.material.Text(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 14.dp),
            text = stringResource(R.string.new_playlist),
            color = LocalCustomColors.current.buttonTextColor,
            style = LocalTypography.current.medium14
        )
    }
}


@Composable
fun EmptyPlaylistsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 46.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_error_search),
            contentDescription = null,
            modifier = Modifier
                .wrapContentSize()
                .padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.no_playlists_message),
            color = LocalCustomColors.current.textColor,
            style = LocalTypography.current.medium18,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 32.dp)
        )
    }
}


@Composable
fun PlaylistItem(
    playlist: Playlist, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.Start
    ) {
        AsyncImage(
            model = playlist.imagePath,
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .aspectRatio(1f),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.mock_image),
            placeholder = painterResource(R.drawable.mock_image)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = playlist.name,
            style = LocalTypography.current.regular12,
            fontWeight = FontWeight.Normal,
            color = LocalCustomColors.current.selectedContent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        // Количество треков
        Text(
            text = LocalContext.current.resources.getQuantityString(
                R.plurals.track, playlist.tracksCount, playlist.tracksCount
            ),
            style = LocalTypography.current.regular12,
            fontWeight = FontWeight.Normal,
            color = LocalCustomColors.current.selectedContent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

