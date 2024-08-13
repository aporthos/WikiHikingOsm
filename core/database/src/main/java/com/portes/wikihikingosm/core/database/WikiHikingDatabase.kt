package com.portes.wikihikingosm.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.portes.wikihikingosm.core.database.dao.HikeDao
import com.portes.wikihikingosm.core.database.dao.RouteDao
import com.portes.wikihikingosm.core.database.dao.WayPointsDao
import com.portes.wikihikingosm.core.database.entities.HikeEntity
import com.portes.wikihikingosm.core.database.entities.RouteEntity
import com.portes.wikihikingosm.core.database.entities.WayPointsEntity

@Database(
    entities = [HikeEntity::class, RouteEntity::class, WayPointsEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(GeoPointConverters::class)
abstract class WikiHikingDatabase : RoomDatabase() {
    abstract fun hikeDao(): HikeDao
    abstract fun routeDao(): RouteDao
    abstract fun wayPointsDao(): WayPointsDao
}