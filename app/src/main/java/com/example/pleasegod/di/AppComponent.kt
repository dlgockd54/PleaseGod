package com.example.pleasegod.di

import com.example.pleasegod.viewmodel.RestroomViewModel
import dagger.Component

/**
 * Created by hclee on 2019-08-16.
 */

@Component(modules = [RestroomRepositoryModule::class])
interface AppComponent {
    fun inject(viewModel: RestroomViewModel)
}