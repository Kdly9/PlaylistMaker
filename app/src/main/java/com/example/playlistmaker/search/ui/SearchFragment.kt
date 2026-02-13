package com.example.playlistmaker.search.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.theme.AppTheme
import com.example.playlistmaker.theme.Blue
import com.example.playlistmaker.theme.LocalCustomColors
import com.example.playlistmaker.theme.LocalTypography
import com.example.playlistmaker.theme.SilverGray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Locale


class SearchFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme(darkTheme = isSystemInDarkTheme()) {
                    SearchScreen(
                        searchViewModel = koinViewModel(),
                        navigateToPlayer = { track ->
                            findNavController().navigate(
                                R.id.action_searchFragment_to_playerFragment,
                                PlayerFragment.createArgs(track)
                            )
                        }
                    )
                }
            }
        }
    }


    @Composable
    private fun SearchScreen(
        searchViewModel: SearchViewModel,
        navigateToPlayer: (Track) -> Unit
    ) {

        var text by remember { mutableStateOf(searchViewModel.getCurrentSearchText()) }
        val state = searchViewModel.searchState.collectAsState().value
        LaunchedEffect(Unit) {
            searchViewModel.restoreSearchState()
        }

        val debounceClickJob = remember { mutableStateOf<Job?>(null) }
        val debouncedClick: (Track) -> Unit = remember {
            { track ->
                debounceClickJob.value?.cancel()

                debounceClickJob.value = CoroutineScope(Dispatchers.Main).launch {
                    delay(CLICK_DEBOUNCE_DELAY)
                    searchViewModel.onTrackClick(track)
                    navigateToPlayer(track)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Tittle()
            SearchTextField(
                searchViewModel = searchViewModel,
                searchText = text,
                onTextChange = { newText ->
                    text = newText
                }
            )
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (state) {
                    is TracksState.History -> {
                        if (state.tracks.isNotEmpty()) {
                            History(
                                tracks = state.tracks,
                                onClearHistoryClick = searchViewModel::onHistoryClear,
                                onTrackClick = debouncedClick
                            )
                        }
                    }

                    is TracksState.Content -> {
                        TracksList(
                            tracks = state.tracks,
                            onTrackClick = debouncedClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    is TracksState.Empty -> {
                        ErrorEmpty()
                    }

                    is TracksState.Failure -> {
                        ErrorConnect(searchViewModel::updateSearch)
                    }

                    is TracksState.Loading -> {
                        LoadingView()
                    }
                }
            }
        }
    }

    @Composable
    private fun LoadingView() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(44.dp),
                color = Color(0xFF3772E7),
                strokeWidth = 4.dp,
            )
        }
    }


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


    @Composable
    private fun Tittle() {
        Text(
            text = stringResource(R.string.title_search),
            modifier = Modifier
                .height(56.dp)
                .padding(start = 16.dp)
                .wrapContentHeight(Alignment.CenterVertically),
            style = LocalTypography.current.titleMedium,
            color = LocalCustomColors.current.textColor
        )
    }

    @Composable
    private fun SearchTextField(
        searchViewModel: SearchViewModel?,
        searchText: String,
        onTextChange: (String) -> Unit
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = { newText ->
                searchViewModel?.searchDebounce(newText)
                onTextChange(newText)
            },
            singleLine = true,
            cursorBrush = SolidColor(Blue),
            textStyle = LocalTypography.current.regular16Black,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LocalCustomColors.current.searchBg)
                        .onFocusEvent { focusState ->
                            searchViewModel?.showHistory(focusState.hasFocus)
                        }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Иконка поиска
                    Icon(
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = null,
                        tint = LocalCustomColors.current.searchIconTint,
                        modifier = Modifier.size(20.dp)
                    )

                    // Контейнер для текста
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        // Placeholder
                        if (searchText.isEmpty()) {
                            Text(
                                text = stringResource(R.string.title_search),
                                style = LocalTypography.current.regular16,
                                color = SilverGray
                            )
                        }
                        innerTextField()
                    }

                    // Кнопка очистки
                    if (searchText.isNotEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear),
                            contentDescription = null,
                            tint = LocalCustomColors.current.searchIconTint,
                            modifier = Modifier
                                .size(12.dp)
                                .clickable {
                                    onTextChange("")
                                    searchViewModel?.clearCurrentSearchText()
                                    searchViewModel?.showHistory(true)
                                    searchViewModel?.clearCurrentSearchText()
                                }
                        )
                    }
                }
            }
        )
    }

    object TimeFormatter {
        private val dateFormat by lazy {
            SimpleDateFormat("mm:ss", Locale.getDefault())
        }

        fun formatTime(milliseconds: Long): String {
            return dateFormat.format(milliseconds)
        }
    }

    @Composable
    private fun History(
        tracks: List<Track>,
        onClearHistoryClick: () -> Unit,
        onTrackClick: ((Track) -> Unit)?
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.looking_for),
                    modifier = Modifier
                        .padding(
                            top = 24.dp,
                            bottom = 8.dp
                        ),
                    color = LocalCustomColors.current.historyTitleColor,
                    fontSize = 19.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium))
                )

                TracksList(
                    tracks = tracks,
                    modifier = Modifier.weight(1f),
                    onTrackClick = onTrackClick
                )

                Button(
                    onClick = { onClearHistoryClick() },
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 16.dp)
                        .wrapContentWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = LocalCustomColors.current.historyTitleColor
                    ),
                    shape = RoundedCornerShape(54.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 10.dp, horizontal = 14.dp),
                        text = stringResource(R.string.clear_history),
                        color = LocalCustomColors.current.buttonTextColor,
                        style = LocalTypography.current.medium14
                    )
                }
            }
        }
    }


    @Composable
    fun ErrorEmpty() {
        Column {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(102.dp)
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.ic_error_search),
                contentDescription = null,
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = stringResource(R.string.nothing_was_found),
                textAlign = TextAlign.Center,
                style = LocalTypography.current.medium19
            )
        }
    }

    @Composable
    fun ErrorConnect(onButtonClick: () -> Unit) {
        Column {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(102.dp)
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.ic_error_connect),
                contentDescription = null,
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = stringResource(R.string.connect_error),
                textAlign = TextAlign.Center,
                style = LocalTypography.current.medium19
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )
            Button(
                onClick = { onButtonClick() },
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = LocalCustomColors.current.historyTitleColor
                ),
                shape = RoundedCornerShape(54.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    text = stringResource(R.string.update),
                    color = LocalCustomColors.current.buttonTextColor,
                    style = LocalTypography.current.medium14
                )
            }

        }
    }

    @Composable
    @Preview(showBackground = true, apiLevel = 35)
    fun SearchScreenPreview() {

        val tracks = listOf(
            Track(
                trackId = "1",
                trackName = "Smells Like Teettttпппппппппппппппппппппtt",
                artistName = "Nirvana",
                trackTimeMillis = "5:01",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg",
                collectionName = "Nevermind",
                releaseDate = "1991-09-24T12:00:00Z",
                primaryGenreName = "Grunge",
                country = "USA",
                previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/mzaf_1234567890.m4a",
                isFavorite = false
            ),
            Track(
                trackId = "2",
                trackName = "Billie Jean",
                artistName = "Michael Jackson",
                trackTimeMillis = "4:35",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg",
                collectionName = "Thriller",
                releaseDate = "1982-11-30T12:00:00Z",
                primaryGenreName = "Pop",
                country = "USA",
                previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/mzaf_1234567891.m4a",
                isFavorite = false
            ),
            Track(
                trackId = "3",
                trackName = "Stayin' Alive",
                artistName = "Bee Gees",
                trackTimeMillis = "4:10",
                artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg",
                collectionName = "Saturday Night Fever",
                releaseDate = "1977-12-13T12:00:00Z",
                primaryGenreName = "Disco",
                country = "USA",
                previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/mzaf_1234567892.m4a",
                isFavorite = false
            ),
            Track(
                trackId = "4",
                trackName = "Whole Lotta Love",
                artistName = "Led Zeppelin",
                trackTimeMillis = "5:33",
                artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg",
                collectionName = "Led Zeppelin II",
                releaseDate = "1969-10-22T12:00:00Z",
                primaryGenreName = "Rock",
                country = "UK",
                previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzaf_1234567893.m4a",
                isFavorite = false
            ),
            Track(
                trackId = "5",
                trackName = "Sweet Child O'Mine",
                artistName = "Guns N' Roses",
                trackTimeMillis = "5:03",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg",
                collectionName = "Appetite for Destruction",
                releaseDate = "1987-07-21T12:00:00Z",
                primaryGenreName = "Rock",
                country = "USA",
                previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/mzaf_1234567894.m4a",
                isFavorite = false
            )
        )

        AppTheme(false) {
            LoadingView()
        }
    }
}