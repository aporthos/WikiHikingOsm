package com.portes.wikihikingosm.core.data.repositories

import com.portes.wikihikingosm.core.database.dao.WayPointsDao
import com.portes.wikihikingosm.core.database.entities.WayPointsEntity
import javax.inject.Inject

class WayPointsRepositoryImpl @Inject constructor(
    private val dao: WayPointsDao,
) : WayPointsRepository {

    override suspend fun addWayPoints(idHikeRoute: Long): Long {
        dao.insertOrIgnoreWayPoints(dataDmmy())
        return 0
    }

    fun dataDmmy() = listOf(
        WayPointsEntity(
            idHikeRoute = 1,
            name = "Indicaciones Zaldiaran",
            latitude = 42.808574,
            longitude = -2.71857
        ),
        WayPointsEntity(
            idHikeRoute = 1,
            name = "San Kiliz - Alto de las Quemadas (836m)",
            latitude = 42.802547,
            longitude = -2.721202
        ),
        WayPointsEntity(
            idHikeRoute = 1,
            name = "Hayedo",
            latitude = 42.798163,
            longitude = -2.729574
        ),
        WayPointsEntity(
            idHikeRoute = 1,
            name = "Zaldiaran (978m)",
            latitude = 42.794739,
            longitude = -2.736595
        ),
        WayPointsEntity(
            idHikeRoute = 1,
            name = "Seguir GR-282 hacia Okina",
            latitude = 42.808574,
            longitude = -2.71857
        ),
        WayPointsEntity(
            idHikeRoute = 1,
            name = "Indicaciones Zaldiaran",
            latitude = 42.794422,
            longitude = -2.731571
        )
    )

}

interface WayPointsRepository {
    //    fun getRoute(): Flow<List<Route>>
    suspend fun addWayPoints(idHikeRoute: Long): Long
}