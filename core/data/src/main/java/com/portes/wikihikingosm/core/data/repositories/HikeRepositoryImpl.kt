package com.portes.wikihikingosm.core.data.repositories

import com.portes.wikihikingosm.core.database.dao.HikeDao
import com.portes.wikihikingosm.core.database.entities.asEntity
import com.portes.wikihikingosm.core.database.entities.asModel
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.HikeWithRoute
import com.portes.wikihikingosm.core.models.HikeWithWayPoints
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HikeRepositoryImpl @Inject constructor(
    private val dao: HikeDao
) : HikeRepository {

    override fun getHikes(): Flow<List<Hike>> = dao.getHikes().map { it.map { it.asModel() } }

    override fun getHikeWithRoute(idHike: Long): Flow<HikeWithRoute> =
        dao.getHikeWithRoute(idHike).map { it.asModel() }

    override fun getHikeWithWayPoints(idHike: Long): Flow<HikeWithWayPoints> =
        dao.getHikeWithWayPoints(idHike).map { it.asModel() }

    override suspend fun addHike(hike: Hike): Long = dao.insertOrIgnoreHike(hike.asEntity())

    override suspend fun canAddHike(hike: Hike): Boolean =
        dao.isSavedHike(name = hike.asEntity().name) == 0
}

interface HikeRepository {
    fun getHikes(): Flow<List<Hike>>
    fun getHikeWithRoute(idHike: Long): Flow<HikeWithRoute>
    fun getHikeWithWayPoints(idHike: Long): Flow<HikeWithWayPoints>
    suspend fun addHike(hike: Hike): Long
    suspend fun canAddHike(hike: Hike): Boolean
}