package com.portes.wikihikingosm.core.data.repositories

import com.portes.wikihikingosm.core.database.dao.RouteDao
import com.portes.wikihikingosm.core.database.entities.asEntity
import com.portes.wikihikingosm.core.database.entities.asModel
import com.portes.wikihikingosm.core.models.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val dao: RouteDao
) : RouteRepository {
    override fun getRoute(): Flow<List<Route>> = dao.getRoute().map { it.map { it.asModel() } }

    override suspend fun addRoute(route: Route): Long = dao.insertOrIgnorePhrase(route.asEntity())
}

interface RouteRepository {
    fun getRoute(): Flow<List<Route>>
    suspend fun addRoute(route: Route): Long
}