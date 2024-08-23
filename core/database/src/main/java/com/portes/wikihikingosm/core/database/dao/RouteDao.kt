package com.portes.wikihikingosm.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.portes.wikihikingosm.core.database.entities.RouteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreRoute(route: RouteEntity): Long

    @Insert
    suspend fun insertOrIgnoreRoute(route: List<RouteEntity>)

    @Transaction
    @Query(
        value = """ 
            SELECT * FROM route
    """
    )
    fun getRoute(): Flow<List<RouteEntity>>

    @Query(
        value = """
            DELETE FROM route 
            WHERE idHikeRoute = :idHike
    """
    )
    fun deleteRouteById(idHike: Long): Int
}