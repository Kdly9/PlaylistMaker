package com.example.playlistmaker.media.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.playlistmaker.media.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.media.view_model.NewPlaylistViewModel
import com.example.playlistmaker.media.view_model.PlaylistsViewModel


val mediaLibraryViewModelModule = module {
    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel{
        NewPlaylistViewModel(get())
    }
}