package com.portes.wikihikingosm.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.portes.wikihikingosm.core.models.Route

@Entity(
    tableName = "route",
)
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double
)

fun RouteEntity.asModel() = Route(
    latitude = latitude,
    longitude = longitude
)

fun Route.asEntity() = RouteEntity(
    latitude = latitude,
    longitude = longitude
)