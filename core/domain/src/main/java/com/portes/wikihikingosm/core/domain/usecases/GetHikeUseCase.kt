package com.portes.wikihikingosm.core.domain.usecases

import com.portes.wikihikingosm.core.common.TYPE_GO
import com.portes.wikihikingosm.core.common.TYPE_RETURN
import com.portes.wikihikingosm.core.common.di.IoDispatcher
import com.portes.wikihikingosm.core.common.domain.FlowSingleUseCase
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
) : FlowSingleUseCase<Long, HikeData>(dispatcher) {

    override fun execute(params: Long): Flow<HikeData> = combine(
        repository.getHikeWithRoute(params),
        repository.getHikeWithWayPoints(params)
    ) { route, wayPoints ->
        val hikeData = HikeData(
            hike = route.hike,
            routeGo = route.route.filter { it.type == TYPE_GO },
            routeReturn = route.route.filter { it.type == TYPE_RETURN },
            wayPoints = wayPoints.wayPoints
        )
        hikeData
    }
}

data class HikeData(
    val hike: Hike,
    val routeGo: List<Route>,
    val routeReturn: List<Route>,
    val wayPoints: List<WayPoints>
)