package com.portes.wikihikingosm.feature.hikings.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.portes.wikihikingosm.feature.hikings.HikingRoutePref.Companion.HIKING_PREFERENCES
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object HikingModule {
    @Provides
    fun provides(@ApplicationContext context: Context): Resources = context.resources

    @Provides
    @Named(HIKING_PREFERENCES)
    fun provideLoginSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(HIKING_PREFERENCES, Context.MODE_PRIVATE)
}