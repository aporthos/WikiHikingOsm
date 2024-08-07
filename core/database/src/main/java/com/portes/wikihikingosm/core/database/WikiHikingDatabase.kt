package com.portes.wikihikingosm.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.portes.wikihikingosm.core.database.dao.HikeDao
import com.portes.wikihikingosm.core.database.dao.RouteDao
import com.portes.wikihikingosm.core.database.entities.HikeEntity
import com.portes.wikihikingosm.core.database.entities.RouteEntity

@Database(
    entities = [HikeEntity::class, RouteEntity::class],
    version = 1,
    exportSchema = true
)
abstract class WikiHikingDatabase : RoomDatabase() {
    abstract fun hikeDao(): HikeDao
    abstract fun routeDao(): RouteDao
}