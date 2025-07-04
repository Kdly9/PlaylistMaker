package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val searchViewModelModule = module {

    viewModelOf(::SearchViewModel)

}