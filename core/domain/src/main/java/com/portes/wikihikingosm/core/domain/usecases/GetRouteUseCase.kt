package com.portes.wikihikingosm.core.domain.usecases

import com.portes.wikihikingosm.core.common.di.IoDispatcher
import com.portes.wikihikingosm.core.common.domain.FlowSingleUseCase
import com.portes.wikihikingosm.core.common.domain.None
import com.portes.wikihikingosm.core.data.repositories.RouteRepository
import com.portes.wikihikingosm.core.models.Route
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRouteUseCase @Inject constructor(
    private val repository: RouteRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FlowSingleUseCase<None, List<Route>>(dispatcher) {

    override fun execute(params: None): Flow<List<Route>> = repository.getRoute()

}