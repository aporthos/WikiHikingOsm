package com.portes.wikihikingosm.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.portes.wikihikingosm.core.database.entities.HikeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HikeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnorePhrase(hike: HikeEntity): Long

    @Transaction
    @Query(
        value = """ 
            SELECT * FROM hike
    """
    )
    fun getHike(): Flow<List<HikeEntity>>
}