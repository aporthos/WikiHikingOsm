package com.portes.wikihikingosm.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.portes.wikihikingosm.feature.hikings.HIKES_ROUTE
import com.portes.wikihikingosm.feature.weather.WEATHER_ROUTE

val mainDestinations: Array<MainSections> = MainSections.values()

enum class MainSections(
    val title: String,
    val icon: ImageVector,
    val route: String,
) {
    HIKES("Caminatas", Icons.Filled.Settings, HIKES_ROUTE),
    WEATHER("Clima", Icons.Filled.Home, WEATHER_ROUTE),
}