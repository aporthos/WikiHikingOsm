package com.portes.wikihikingosm.feature.weather

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val WEATHER_ROUTE = "WEATHER"

fun NavGraphBuilder.weatherScreen() {
    composable(WEATHER_ROUTE) {
        WeatherScreen()
    }
}