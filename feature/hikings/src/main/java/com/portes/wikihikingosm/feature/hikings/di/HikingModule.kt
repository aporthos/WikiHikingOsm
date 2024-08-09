package com.portes.wikihikingosm.feature.hikings.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HikingModule {
    @Provides
    fun provides(@ApplicationContext context: Context): Resources = context.resources
}