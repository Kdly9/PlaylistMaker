package com.example.playlistmaker.media.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FavoritesTracksInteractor
import com.example.playlistmaker.media.ui.fragments.FavouritesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favouritesTracksInteractor: FavoritesTracksInteractor
) : ViewModel() {

    private val _stateLiveData = MutableStateFlow<FavouritesState>(FavouritesState.Empty)
    val observeState = _stateLiveData.asStateFlow()

    init {
        viewModelScope.launch {
            getTracks()
        }
    }

    private suspend fun getTracks() {
        favouritesTracksInteractor.getTracks().collect { favouriteList ->
            _stateLiveData.value = if (favouriteList.isEmpty()) {
                FavouritesState.Empty
            } else {
                FavouritesState.Content(favouriteList)
            }
        }
    }
}