package com.portes.wikihikingosm.core.data.repositories

import android.content.Context
import android.content.res.AssetManager
import com.portes.wikihikingosm.core.database.dao.HikeDao
import com.portes.wikihikingosm.core.database.entities.asEntity
import com.portes.wikihikingosm.core.database.entities.asModel
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.HikeWithRoute
import com.portes.wikihikingosm.core.models.HikeWithWayPoints
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ticofab.androidgpxparser.parser.GPXParser
import io.ticofab.androidgpxparser.parser.domain.Gpx
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import timber.log.Timber
import javax.inject.Inject

class HikeRepositoryImpl @Inject constructor(
    private val dao: HikeDao,
    private val routeRepository: RouteRepository,
    private val wayPointsRepository: WayPointsRepository,
    @ApplicationContext private val context: Context,
    private val assets: AssetManager,
    private val parser: GPXParser
) : HikeRepository {

    override fun getHikes(): Flow<List<Hike>> = dao.getHikes().map { it.map { it.asModel() } }

    override fun getHikeWithRoute(idHike: Long): Flow<HikeWithRoute> =
        dao.getHikeWithRoute(idHike).map { it.asModel() }

    override fun getHikeWithWayPoints(idHike: Long): Flow<HikeWithWayPoints> =
        dao.getHikeWithWayPoints(idHike).map { it.asModel() }

    override suspend fun addHike(hike: Hike): Long {
        val (name, startPoint, distanceTotal) = parser.parse(assets.open(hike.name))?.let {
            Triple(
                it.tracks.firstOrNull()?.trackName.orEmpty(),
                GeoPoint(
                    it.tracks.firstOrNull()?.trackSegments?.firstOrNull()?.trackPoints?.firstOrNull()?.latitude
                        ?: 0.0,
                    it.tracks.firstOrNull()?.trackSegments?.firstOrNull()?.trackPoints?.firstOrNull()?.longitude
                        ?: 0.0
                ),
                getTotalDistance(it.getPoints())
            )

        } ?: run {
            Triple("Default name", GeoPoint(0.0, 0.0), 0.0)
        }
        val idHike =
            dao.canInsertHike(Hike(name = name, startPoint = startPoint, distanceTotal = distanceTotal).asEntity())
//        wayPointsRepository.addWayPoints(1)
        if (idHike > -1) {
            routeRepository.addRoute(idHike, hike.name)
        }
        return idHike
    }

    private fun getTotalDistance(list: List<GeoPoint>): Double {
        val path = Polyline().apply {
            setPoints(list)
        }
        return path.distance
    }
}

fun Gpx.getPoints(): List<GeoPoint> = mutableListOf<GeoPoint>().apply {
    tracks.firstOrNull()?.trackSegments?.firstOrNull()?.trackPoints?.map {
        add(
            GeoPoint(it.latitude, it.longitude)
        )
    }
}

interface HikeRepository {
    fun getHikes(): Flow<List<Hike>>
    fun getHikeWithRoute(idHike: Long): Flow<HikeWithRoute>
    fun getHikeWithWayPoints(idHike: Long): Flow<HikeWithWayPoints>
    suspend fun addHike(hike: Hike): Long
}