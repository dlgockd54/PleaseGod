package com.example.pleasegod.di

import com.example.pleasegod.model.repository.RestroomRepository
import dagger.Module
import dagger.Provides

/**
 * Created by hclee on 2019-08-16.
 */

@Module
class RestroomRepositoryModule {
    @Provides
    fun provideRestroomRepository(): RestroomRepository =
        RestroomRepository()
}