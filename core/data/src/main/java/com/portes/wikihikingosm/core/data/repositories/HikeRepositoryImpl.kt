package com.portes.wikihikingosm.core.data.repositories

import com.portes.wikihikingosm.core.database.dao.HikeDao
import com.portes.wikihikingosm.core.database.entities.asEntity
import com.portes.wikihikingosm.core.database.entities.asModel
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.HikeWithRoute
import com.portes.wikihikingosm.core.models.HikeWithWayPoints
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class HikeRepositoryImpl @Inject constructor(
    private val dao: HikeDao,
    private val routeRepository: RouteRepository,
    private val wayPointsRepository: WayPointsRepository,
) : HikeRepository {
    override fun getHikeWithRoute(idHike: Int): Flow<HikeWithRoute> =
        dao.getHikeWithRoute(idHike).map { it.asModel() }

    override fun getHikeWithWayPoints(idHike: Int): Flow<HikeWithWayPoints> =
        dao.getHikeWithWayPoints(idHike).map { it.asModel() }

    override fun canHikes(): Flow<Int> = dao.canHikes()

    override suspend fun addHike(hike: Hike): Long {
        val idHike = dao.canInsertHike(hike.asEntity())
//        wayPointsRepository.addWayPoints(1)
        if (idHike > -1) {
            routeRepository.addRoute(idHike, hike.name)
        }
        return idHike
    }
}

interface HikeRepository {
    fun getHikeWithRoute(idHike: Int): Flow<HikeWithRoute>
    fun getHikeWithWayPoints(idHike: Int): Flow<HikeWithWayPoints>
    fun canHikes(): Flow<Int>
    suspend fun addHike(hike: Hike): Long
}