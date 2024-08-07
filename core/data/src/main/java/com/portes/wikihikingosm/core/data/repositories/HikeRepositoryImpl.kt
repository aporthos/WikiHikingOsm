package com.portes.wikihikingosm.core.data.repositories

import com.portes.wikihikingosm.core.database.dao.HikeDao
import com.portes.wikihikingosm.core.database.entities.asEntity
import com.portes.wikihikingosm.core.database.entities.asModel
import com.portes.wikihikingosm.core.models.Hike
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HikeRepositoryImpl @Inject constructor(
    private val dao: HikeDao
) : HikeRepository {
    override fun getHike(): Flow<List<Hike>> = dao.getHike().map { it.map { it.asModel() } }

    override suspend fun addHike(hike: Hike): Long = dao.insertOrIgnorePhrase(hike.asEntity())
}

interface HikeRepository {
    fun getHike(): Flow<List<Hike>>
    suspend fun addHike(hike: Hike): Long
}