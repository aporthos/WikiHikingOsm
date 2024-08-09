package com.portes.wikihikingosm.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.portes.wikihikingosm.core.database.entities.WayPointsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WayPointsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreWayPoints(route: WayPointsEntity): Long

    @Insert
    suspend fun insertOrIgnoreWayPoints(route: List<WayPointsEntity>)

    @Transaction
    @Query(
        value = """ 
            SELECT * FROM wayPoints
    """
    )
    fun getWayPoints(): Flow<List<WayPointsEntity>>
}