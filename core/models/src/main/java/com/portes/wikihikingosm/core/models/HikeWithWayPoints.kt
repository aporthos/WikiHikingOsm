package com.portes.wikihikingosm.core.models

data class HikeWithWayPoints(
    val hike: Hike,
    val wayPoints: List<WayPoints>
)