package com.portes.wikihikingosm.core.database.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.portes.wikihikingosm.core.models.HikeWithRoute
import com.portes.wikihikingosm.core.models.HikeWithWayPoints

data class HikeWithWayPointsRelation(
    @Embedded val hike: HikeEntity,
    @Relation(
        parentColumn = "idHike",
        entityColumn = "idHikeRoute"
    )
    val wayPoints: List<WayPointsEntity>
)


fun HikeWithWayPointsRelation.asModel() = HikeWithWayPoints(
    hike = hike.asModel(),
    wayPoints = wayPoints.map { it.asModel() }
)