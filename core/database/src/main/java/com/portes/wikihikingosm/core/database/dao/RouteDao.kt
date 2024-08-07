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
    suspend fun insertOrIgnorePhrase(route: RouteEntity): Long

    @Transaction
    @Query(
        value = """ 
            SELECT * FROM route
    """
    )
    fun getRoute(): Flow<List<RouteEntity>>
}