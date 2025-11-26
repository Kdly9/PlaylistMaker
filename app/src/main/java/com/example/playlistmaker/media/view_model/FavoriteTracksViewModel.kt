package com.example.playlistmaker.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FavoritesTracksInteractor
import com.example.playlistmaker.media.ui.fragments.FavouritesState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favouritesTracksInteractor: FavoritesTracksInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavouritesState>()
    fun observeState(): LiveData<FavouritesState> = stateLiveData

    init {
        viewModelScope.launch {
            getTracks()
        }
    }

    private suspend fun getTracks() {
        favouritesTracksInteractor.getTracks().collect { favouriteList ->
            stateLiveData.value = if (favouriteList.isEmpty()) {
                FavouritesState.Empty
            } else {
                FavouritesState.Content(favouriteList)
            }
        }
    }
}