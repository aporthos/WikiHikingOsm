package com.portes.wikihikingosm.core.domain.usecases

import com.portes.wikihikingosm.core.common.di.IoDispatcher
import com.portes.wikihikingosm.core.common.domain.FlowSingleUseCase
import com.portes.wikihikingosm.core.common.domain.None
import com.portes.wikihikingosm.core.data.repositories.HikeRepository
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.Route
import com.portes.wikihikingosm.core.models.WayPoints
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetHikeUseCase @Inject constructor(
    private val repository: HikeRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FlowSingleUseCase<None, HikeData>(dispatcher) {

    override fun execute(params: None): Flow<HikeData> = combine(
        repository.getHikeWithRoute(),
        repository.getHikeWithWayPoints()
    ) { route, wayPoints ->
        HikeData(route.hike, route.route, wayPoints.wayPoints)
    }
}

data class HikeData(
    val hike: Hike,
    val route: List<Route>,
    val wayPoints: List<WayPoints>
)