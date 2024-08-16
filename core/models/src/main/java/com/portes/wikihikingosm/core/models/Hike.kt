package com.portes.wikihikingosm.core.models

import org.osmdroid.util.GeoPoint

data class Hike(
    val idHike: Long = 0,
    val name: String,
    val startPoint: GeoPoint,
    val distanceTotal: Double,
    val route: List<Route> = emptyList()
)