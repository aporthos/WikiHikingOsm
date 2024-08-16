package com.portes.wikihikingosm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ticofab.androidgpxparser.parser.GPXParser

@Module
@InstallIn(SingletonComponent::class)
object MainModule {
    @Provides
    fun providesParser() = GPXParser()
}