package com.example.playlistmaker.sharing.di

import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val sharingInteractorModule = module {

    factory<SharingInteractor> {
        SharingInteractorImpl(
            externalNavigator = { activity -> get { parametersOf(activity) } },
            stringProvider = get()
        )
    }

}