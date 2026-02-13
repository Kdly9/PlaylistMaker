package com.example.playlistmaker.media.ui.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.media.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TracksList
import com.example.playlistmaker.theme.LocalCustomColors
import com.example.playlistmaker.theme.LocalTypography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FavouriteTracks(
    tracksViewModel: FavoriteTracksViewModel,
    onTrackClick: (Track) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        val debounceClickJob = remember { mutableStateOf<Job?>(null) }
        val debouncedClick: (Track) -> Unit = remember {
            { track ->
                debounceClickJob.value?.cancel()

                debounceClickJob.value = CoroutineScope(Dispatchers.Main).launch {
                    delay(1000L)
                    onTrackClick(track)
                }
            }
        }

        when (val state = tracksViewModel.observeState.collectAsState().value) {
            is FavouritesState.Content -> {
                TracksList(
                    tracks = state.tracks, onTrackClick = debouncedClick,
                    modifier = Modifier.fillMaxSize()
                )
            }

            FavouritesState.Empty -> {
                Image(
                    painter = painterResource(id = R.drawable.ic_error_search),
                    contentDescription = null,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(top = 106.dp)
                )

                Text(
                    text = stringResource(R.string.media_library_is_empty),
                    color = LocalCustomColors.current.textColor,
                    style = LocalTypography.current.medium18,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 32.dp)
                )
            }
        }
    }
}