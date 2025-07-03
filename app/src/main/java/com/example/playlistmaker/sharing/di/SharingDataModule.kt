package com.example.playlistmaker.sharing.di

import android.content.Context
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.StringProviderImpl
import com.example.playlistmaker.sharing.domain.StringProvider
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import org.koin.dsl.module

val sharingDataModule = module {

    factory<ExternalNavigator> { (context: Context) ->
        ExternalNavigatorImpl(context)
    }

    single<StringProvider> {
        StringProviderImpl(get())
    }

}