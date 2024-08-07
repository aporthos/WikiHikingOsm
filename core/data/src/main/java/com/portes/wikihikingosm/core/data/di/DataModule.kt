package com.portes.wikihikingosm.core.data.di

import com.portes.wikihikingosm.core.data.repositories.HikeRepository
import com.portes.wikihikingosm.core.data.repositories.HikeRepositoryImpl
import com.portes.wikihikingosm.core.data.repositories.RouteRepository
import com.portes.wikihikingosm.core.data.repositories.RouteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsRouteRepository(
        repository: RouteRepositoryImpl,
    ): RouteRepository

    @Binds
    fun bindsHikeRepository(
        repository: HikeRepositoryImpl,
    ): HikeRepository
}