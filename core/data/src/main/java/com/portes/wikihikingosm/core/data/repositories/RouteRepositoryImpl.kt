package com.portes.wikihikingosm.core.data.repositories

import android.content.Context
import com.portes.wikihikingosm.core.database.dao.RouteDao
import com.portes.wikihikingosm.core.database.entities.RouteEntity
import com.portes.wikihikingosm.core.database.entities.asEntity
import com.portes.wikihikingosm.core.database.entities.asModel
import com.portes.wikihikingosm.core.models.Route
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ticofab.androidgpxparser.parser.GPXParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import io.ticofab.androidgpxparser.parser.domain.Gpx
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val dao: RouteDao,
    @ApplicationContext private val context: Context,
    private val parser: GPXParser
) : RouteRepository {
    override fun getRoute(): Flow<List<Route>> = dao.getRoute().map { it.map { it.asModel() } }

    override suspend fun addRoute(idHike: Long, name: String): Long {
        parser.parse(context.resources.assets.open(name))?.let {
            dao.insertOrIgnorePhrase(it.parsed(idHike))
        }
        return 0
    }
}

fun Gpx.parsed(idHike: Long): List<RouteEntity> = mutableListOf<RouteEntity>().apply {
    tracks.firstOrNull()?.trackSegments?.firstOrNull()?.trackPoints?.map {
        add(
            RouteEntity(
                latitude = it.latitude,
                longitude = it.longitude,
                idHikeRoute = idHike,
                elevation = it.elevation
            )
        )
    }
}

interface RouteRepository {
    fun getRoute(): Flow<List<Route>>
    suspend fun addRoute(idHike: Long, name: String): Long
}