package com.example.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TracksState
import com.example.playlistmaker.utils.livedata.SingleLiveEvent

class SearchViewModel(
    private val trackInteractor: TracksInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { itunesResponse() }

    private val searchState = MutableLiveData<TracksState>()
    fun observeSearchState(): LiveData<TracksState> = searchState

    private val runPlayer = SingleLiveEvent<Track>()
    fun observeRunPlayer(): LiveData<Track> = runPlayer

    private var lastText = ""

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

        runPlayer.postValue(track) }

    fun updateSearch() {
        itunesResponse()
    }

    private fun itunesResponse() {
        if (lastText.isNotEmpty()) {

            searchState.postValue(TracksState.Loading)
            trackInteractor.searchTracks(lastText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    searchState.postValue(TracksState.Content(emptyList()))
                    if (foundTracks.isNotEmpty()) {
                        searchState.postValue(TracksState.Content(foundTracks))
                    } else {
                        searchState.postValue(TracksState.Empty)
                    }
                }

                override fun failure() {
                    searchState.postValue(TracksState.Failure)
                }
            })
        }
    }

    fun searchDebounce(newText: String) {
        if (newText != lastText) {
            lastText = newText
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    fun showHistory(show: Boolean) {
        if (show) {
            val history = tracksHistoryInteractor.getHistory() as ArrayList<Track>
            if (history.isEmpty()) {
                searchState.postValue(TracksState.History(emptyList()))
            } else {
                searchState.postValue(TracksState.History(history))
            }

        } else {
            searchState.postValue(TracksState.History(emptyList()))
        }
    }

    override fun onCleared() {
        handler.removeCallbacks(searchRunnable)
    }

    fun onHistoryClear() {
        tracksHistoryInteractor.clearHistory()
        showHistory(true)
    }
}