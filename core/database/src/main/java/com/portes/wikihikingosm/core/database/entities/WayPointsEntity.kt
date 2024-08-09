package com.portes.wikihikingosm.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.portes.wikihikingosm.core.models.WayPoints

@Entity(
    tableName = "wayPoints",
)
data class WayPointsEntity(
    @PrimaryKey(autoGenerate = true) val idWayPoint: Long = 0,
    val idHikeRoute: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

fun WayPointsEntity.asModel() = WayPoints(
    name = name,
    latitude = latitude,
    longitude = longitude,
    idHikeRoute = idHikeRoute
)

fun WayPoints.asEntity() = WayPointsEntity(
    name = name,
    latitude = latitude,
    longitude = longitude,
    idHikeRoute = idHikeRoute
)