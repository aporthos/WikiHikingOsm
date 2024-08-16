package com.portes.wikihikingosm.core.domain.usecases

import com.portes.wikihikingosm.core.common.di.IoDispatcher
import com.portes.wikihikingosm.core.common.domain.UseCase
import com.portes.wikihikingosm.core.data.repositories.HikeRepository
import com.portes.wikihikingosm.core.data.repositories.RouteRepository
import com.portes.wikihikingosm.core.models.Hike
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddHikeUseCase @Inject constructor(
    private val repository: HikeRepository,
    private val routeRepository: RouteRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : UseCase<AddHikeUseCase.Params, Result<Boolean>>(dispatcher) {

    data class Params(
        val hike: Hike
    )

    override suspend fun execute(parameters: Params): Result<Boolean> {
        val canAddHike = repository.canAddHike(parameters.hike)

        if (canAddHike.not()) {
            return Result.failure(Exception("Hike repeated!!"))
        }

        val idHike = repository.addHike(parameters.hike)
        routeRepository.addRoute(idHike = idHike, hike = parameters.hike)

        return Result.success(true)
    }
}