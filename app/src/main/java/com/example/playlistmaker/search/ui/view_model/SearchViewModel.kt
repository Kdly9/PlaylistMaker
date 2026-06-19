package com.example.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TracksState
import com.example.playlistmaker.utils.debounce
import com.example.playlistmaker.utils.livedata.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SearchViewModel(
    private val trackInteractor: TracksInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor
) : ViewModel() {

    private val _searchState = MutableStateFlow<TracksState>(TracksState.Empty)
    val searchState = _searchState.asStateFlow()

    private val runPlayer = SingleLiveEvent<Track>()
    fun observeRunPlayer(): LiveData<Track> = runPlayer

    private var lastText = ""
    private val onSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { text ->
            itunesResponse(text)
        }

    private var currentSearchText: String = ""


    fun clearCurrentSearchText() {
        currentSearchText = ""
    }

    fun getCurrentSearchText():String {
        return currentSearchText
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    fun onTrackClick(track: Track) {
        val tracksHistory = tracksHistoryInteractor.getHistory() as ArrayList<Track>
        if (tracksHistory.size >= 10) {
            tracksHistory.removeAt(tracksHistory.size - 1)
        }

        if (tracksHistory.any { it.trackId == track.trackId }) {
            tracksHistory.remove(track)
        }
        tracksHistory.add(0, track)
        tracksHistoryInteractor.saveHistory(tracksHistory)

        runPlayer.postValue(track)
    }

    fun updateSearch() {
        itunesResponse(lastText)
    }

    private fun itunesResponse(text: String) {
        if (text.isNotEmpty()) {

            _searchState.value = TracksState.Loading

            viewModelScope.launch {
                trackInteractor
                    .searchTracks(text)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                _searchState.value = TracksState.Failure
            }

            tracks.isEmpty() -> {
                _searchState.value = TracksState.Empty
            }

            else -> {
                _searchState.value = TracksState.Content(tracks)
            }
        }
    }

    fun searchDebounce(newText: String) {
        if (newText != lastText) {
            lastText = newText
            currentSearchText = newText
            onSearchDebounce(lastText)
        }
    }

    fun restoreSearchState() {
        if (currentSearchText.isNotEmpty()) {
            onSearchDebounce(currentSearchText)
        } else {
            showHistory(true)
        }
    }

    fun showHistory(show: Boolean) {
        if (show && currentSearchText.isEmpty()) {
            val history = tracksHistoryInteractor.getHistory() as ArrayList<Track>
            if (history.isEmpty()) {
                _searchState.value = TracksState.History(emptyList())
            } else {
                _searchState.value = TracksState.History(history)
            }

        } else {
            _searchState.value = TracksState.History(emptyList())
        }
    }

    fun onHistoryClear() {
        tracksHistoryInteractor.clearHistory()
        showHistory(true)
    }
}