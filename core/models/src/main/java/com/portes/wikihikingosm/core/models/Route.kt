package com.portes.wikihikingosm.core.models

data class Route(
    var idHikeRoute: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val type: String
)