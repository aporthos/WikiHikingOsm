package com.portes.wikihikingosm.core.domain.usecases

import com.portes.wikihikingosm.core.common.di.IoDispatcher
import com.portes.wikihikingosm.core.common.domain.UseCase
import com.portes.wikihikingosm.core.data.repositories.RouteRepository
import com.portes.wikihikingosm.core.models.Route
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddRouteUseCase @Inject constructor(
    private val repository: RouteRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : UseCase<AddRouteUseCase.Params, Long>(dispatcher) {

    data class Params(
        val route: Route
    )

    override suspend fun execute(parameters: Params): Long = repository.addRoute(parameters.route)
}