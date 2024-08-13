package com.portes.wikihikingosm.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.portes.wikihikingosm.core.database.GeoPointConverters
import com.portes.wikihikingosm.core.models.Hike
import org.osmdroid.util.GeoPoint

@Entity(
    tableName = "hike",
)
data class HikeEntity(
    @PrimaryKey(autoGenerate = true) val idHike: Long = 0,
    val name: String,
    @TypeConverters(GeoPointConverters::class)
    val startPoint: GeoPoint,
    val distanceTotal: Double,
)

fun HikeEntity.asModel() = Hike(
    name = name,
    idHike = idHike,
    startPoint = startPoint,
    distanceTotal = distanceTotal
)

fun Hike.asEntity() = HikeEntity(
    name = name,
    idHike = idHike,
    startPoint = startPoint,
    distanceTotal = distanceTotal
)