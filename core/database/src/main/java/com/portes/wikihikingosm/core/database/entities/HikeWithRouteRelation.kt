package com.portes.wikihikingosm.core.database.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.portes.wikihikingosm.core.models.HikeWithRoute

data class HikeWithRouteRelation(
    @Embedded val hike: HikeEntity,
    @Relation(
        parentColumn = "idHike",
        entityColumn = "idHikeRoute"
    )
    val route: List<RouteEntity>
)


fun HikeWithRouteRelation.asModel() = HikeWithRoute(
    hike = hike.asModel(),
    route = route.map { it.asModel() }
)