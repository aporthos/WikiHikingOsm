package com.portes.wikihikingosm.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.portes.wikihikingosm.core.models.Route

@Entity(
    tableName = "route",
)
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val idRoute: Long = 0,
    val idHikeRoute: Long,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val type: String
)

fun RouteEntity.asModel() = Route(
    latitude = latitude,
    longitude = longitude,
    idHikeRoute = idHikeRoute,
    elevation = elevation,
    type = type
)

fun Route.asEntity() = RouteEntity(
    latitude = latitude,
    longitude = longitude,
    idHikeRoute = idHikeRoute,
    elevation = elevation,
    type = type
)