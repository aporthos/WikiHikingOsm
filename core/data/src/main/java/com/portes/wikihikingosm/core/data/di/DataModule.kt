package com.portes.wikihikingosm.core.data.di

import com.portes.wikihikingosm.core.data.repositories.HikeRepository
import com.portes.wikihikingosm.core.data.repositories.HikeRepositoryImpl
import com.portes.wikihikingosm.core.data.repositories.RouteRepository
import com.portes.wikihikingosm.core.data.repositories.RouteRepositoryImpl
import com.portes.wikihikingosm.core.data.repositories.WayPointsRepository
import com.portes.wikihikingosm.core.data.repositories.WayPointsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Module
    @InstallIn(SingletonComponent::class)
    internal interface DataModuleBinder {
        @Binds
        fun bindsRouteRepository(
            repository: RouteRepositoryImpl,
        ): RouteRepository

        @Binds
        fun bindsHikeRepository(
            repository: HikeRepositoryImpl,
        ): HikeRepository

        @Binds
        fun bindsWayPointsRepositoryRepository(
            repository: WayPointsRepositoryImpl,
        ): WayPointsRepository
    }
}