package com.portes.wikihikingosm.core.domain.usecases.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.portes.wikihikingosm.core.domain.usecases.HikingRoutePref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    fun provides(@ApplicationContext context: Context): Resources = context.resources

    @Provides
    @Named(HikingRoutePref.HIKING_PREFERENCES)
    fun provideLoginSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(HikingRoutePref.HIKING_PREFERENCES, Context.MODE_PRIVATE)
}