package com.example.playlistmaker.sharing.di

import android.app.Activity
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.StringProviderImpl
import com.example.playlistmaker.sharing.domain.StringProvider
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import org.koin.dsl.module

val sharingDataModule = module {

    factory<ExternalNavigator> {(activity: Activity)->
        ExternalNavigatorImpl(activity)
    }

    single<StringProvider> {
        StringProviderImpl(get())
    }

}