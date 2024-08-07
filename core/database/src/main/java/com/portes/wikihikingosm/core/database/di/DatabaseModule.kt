package com.portes.wikihikingosm.core.database.di

import android.content.Context
import androidx.room.Room
import com.portes.wikihikingosm.core.database.WikiHikingDatabase
import com.portes.wikihikingosm.core.database.dao.HikeDao
import com.portes.wikihikingosm.core.database.dao.RouteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesUfcTrackerDatabase(
        @ApplicationContext context: Context,
    ): WikiHikingDatabase = Room.databaseBuilder(
        context = context, WikiHikingDatabase::class.java,
        "WikiHikingDatabase.db",
    )
        .build()

    @Provides
    @Singleton
    fun providesHikeDao(
        database: WikiHikingDatabase,
    ): HikeDao = database.hikeDao()

    @Provides
    @Singleton
    fun providesRouteDao(
        database: WikiHikingDatabase,
    ): RouteDao = database.routeDao()
}