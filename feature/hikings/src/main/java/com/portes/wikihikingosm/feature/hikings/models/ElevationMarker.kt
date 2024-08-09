package com.portes.wikihikingosm.feature.hikings.models

import org.osmdroid.util.GeoPoint

data class ElevationMarker(
    override val position: GeoPoint,
    override val icon: Int,
    override val text: String,
) : CustomMarker

data class WayPointMarker(
    override val position: GeoPoint,
    override val icon: Int,
    override val text: String,
) : CustomMarker


interface CustomMarker {
    val position: GeoPoint
    val icon: Int
    val text: String
}