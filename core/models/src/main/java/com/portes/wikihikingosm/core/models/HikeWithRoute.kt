package com.portes.wikihikingosm.core.models

data class HikeWithRoute(
    val hike: Hike,
    val route: List<Route>
)