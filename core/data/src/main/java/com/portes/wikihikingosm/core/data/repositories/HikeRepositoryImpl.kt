package com.portes.wikihikingosm.core.data.repositories

import com.portes.wikihikingosm.core.database.dao.HikeDao
import com.portes.wikihikingosm.core.database.entities.asEntity
import com.portes.wikihikingosm.core.database.entities.asModel
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.HikeWithRoute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class HikeRepositoryImpl @Inject constructor(
    private val dao: HikeDao,
    private val routeRepository: RouteRepository
) : HikeRepository {
    override fun getHike(): Flow<HikeWithRoute> =
        dao.getHikeWithRoute().map { it.asModel() }

    override suspend fun addHike(hike: Hike): Long {
        val idHike = dao.insertOrIgnorePhrase(hike.asEntity())
        Timber.i("idHike $idHike")
        routeRepository.addRoute(idHike)
        return idHike
    }
}

interface HikeRepository {
    fun getHike(): Flow<HikeWithRoute>
    suspend fun addHike(hike: Hike): Long
}