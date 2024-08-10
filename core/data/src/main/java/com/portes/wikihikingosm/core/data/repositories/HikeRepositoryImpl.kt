package com.portes.wikihikingosm.core.data.repositories

import android.content.Context
import com.portes.wikihikingosm.core.database.dao.HikeDao
import com.portes.wikihikingosm.core.database.entities.asEntity
import com.portes.wikihikingosm.core.database.entities.asModel
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.HikeWithRoute
import com.portes.wikihikingosm.core.models.HikeWithWayPoints
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ticofab.androidgpxparser.parser.GPXParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class HikeRepositoryImpl @Inject constructor(
    private val dao: HikeDao,
    private val routeRepository: RouteRepository,
    private val wayPointsRepository: WayPointsRepository,
    @ApplicationContext private val context: Context,
    private val parser: GPXParser
) : HikeRepository {

    override fun getHikes(): Flow<List<Hike>> = dao.getHikes().map { it.map { it.asModel() } }

    override fun getHikeWithRoute(idHike: Long): Flow<HikeWithRoute> =
        dao.getHikeWithRoute(idHike).map { it.asModel() }

    override fun getHikeWithWayPoints(idHike: Long): Flow<HikeWithWayPoints> =
        dao.getHikeWithWayPoints(idHike).map { it.asModel() }

    override suspend fun addHike(hike: Hike): Long {
        val name = parser.parse(context.resources.assets.open(hike.name))?.let {
            it.tracks.firstOrNull()?.trackName
        } ?: run {
            "Default name"
        }
        val idHike = dao.canInsertHike(Hike(name = name).asEntity())
//        wayPointsRepository.addWayPoints(1)
        if (idHike > -1) {
            routeRepository.addRoute(idHike, hike.name)
        }
        return idHike
    }
}

interface HikeRepository {
    fun getHikes(): Flow<List<Hike>>
    fun getHikeWithRoute(idHike: Long): Flow<HikeWithRoute>
    fun getHikeWithWayPoints(idHike: Long): Flow<HikeWithWayPoints>
    suspend fun addHike(hike: Hike): Long
}