package com.example.playlistmaker.media.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.media.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.theme.LocalCustomColors
import com.example.playlistmaker.theme.LocalTypography
import kotlinx.coroutines.launch


@Composable
fun MediaLibraryScreen(
    tracksViewModel: FavoriteTracksViewModel,
    playlistsViewModel: PlaylistsViewModel,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onNewPlaylistClick: () -> Unit
) {
    // CoroutineScope нам нужен для анимации при переключении табов
    val scope = rememberCoroutineScope()

    // Ключевое состояние Pager, при инициализации которого указывается количество экранов, которые можно переключать
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage = 0)

    // Индекс текущего выбранного таба
    val selectedTabIndex = remember { mutableIntStateOf(pagerState.currentPage) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    )
    {
        Text(
            text = stringResource(R.string.media_library),
            modifier = Modifier
                .height(56.dp)
                .padding(start = 16.dp)
                .wrapContentHeight(Alignment.CenterVertically),
            style = LocalTypography.current.titleMedium,
            color = LocalCustomColors.current.textColor
        )

        TabRow(
            selectedTabIndex = selectedTabIndex.intValue,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .height(2.dp),
                    color = LocalCustomColors.current.selectedContent,

                    )
            }
        ) {
            Tab(
                selected = selectedTabIndex.intValue == 0,
                selectedContentColor = LocalCustomColors.current.selectedContent,
                unselectedContentColor = LocalCustomColors.current.selectedContent,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                text = { Text(text = stringResource(R.string.favourites)) },
                modifier = Modifier.background(LocalCustomColors.current.backgroundColor)
            )

            Tab(
                selected = selectedTabIndex.intValue == 1,
                selectedContentColor = LocalCustomColors.current.selectedContent,
                unselectedContentColor = LocalCustomColors.current.selectedContent,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                text = { Text(text = stringResource(R.string.playlists)) },
                modifier = Modifier.background(LocalCustomColors.current.backgroundColor)
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> FavouriteTracks(tracksViewModel, onTrackClick)
                1 -> MediaPlaylists(playlistsViewModel, onPlaylistClick, onNewPlaylistClick)
            }
        }
    }
}
