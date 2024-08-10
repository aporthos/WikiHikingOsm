package com.portes.wikihikingosm.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.portes.wikihikingosm.core.database.entities.HikeEntity
import com.portes.wikihikingosm.core.database.entities.HikeWithRouteRelation
import com.portes.wikihikingosm.core.database.entities.HikeWithWayPointsRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface HikeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreHike(hike: HikeEntity): Long

    @Query(
        value = """
            SELECT COUNT(*) FROM hike
            WHERE name LIKE :name
    """
    )
    fun isSavedHike(name: String): Int

    suspend fun canInsertHike(hike: HikeEntity): Long {
        if (isSavedHike(name = hike.name) == 0) {
            return insertOrIgnoreHike(hike)
        }
        return -1
    }

    @Transaction
    @Query("SELECT * FROM hike where idHike = :idHike")
    fun getHikeWithRoute(idHike: Long): Flow<HikeWithRouteRelation>

    @Transaction
    @Query("SELECT * FROM hike where idHike = :idHike")
    fun getHikeWithWayPoints(idHike: Long): Flow<HikeWithWayPointsRelation>

    @Transaction
    @Query("SELECT * FROM hike")
    fun getHikes(): Flow<List<HikeEntity>>
}