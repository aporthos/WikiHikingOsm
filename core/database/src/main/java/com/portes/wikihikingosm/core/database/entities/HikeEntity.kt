package com.portes.wikihikingosm.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.portes.wikihikingosm.core.models.Hike

@Entity(
    tableName = "hike",
)
data class HikeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

fun HikeEntity.asModel() = Hike(
    name = name
)

fun Hike.asEntity() = HikeEntity(
    name = name
)