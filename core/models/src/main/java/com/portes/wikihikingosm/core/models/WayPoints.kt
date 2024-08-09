package com.portes.wikihikingosm.core.models

data class WayPoints(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val idHikeRoute: Long,
)