package com.portes.wikihikingosm.core.domain.usecases

import com.portes.wikihikingosm.core.common.di.IoDispatcher
import com.portes.wikihikingosm.core.common.domain.UseCase
import com.portes.wikihikingosm.core.data.repositories.HikeRepository
import com.portes.wikihikingosm.core.data.repositories.RouteRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteHikeByIdUseCase @Inject constructor(
    private val repository: HikeRepository,
    private val routeRepository: RouteRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : UseCase<DeleteHikeByIdUseCase.Params, Result<Boolean>>(dispatcher) {

    data class Params(
        val idHike: Long
    )

    override suspend fun execute(parameters: Params): Result<Boolean> {
        val canDelete = repository.deleteHike(parameters.idHike)
        if (canDelete) {
            routeRepository.deleteRoute(idHike = parameters.idHike)
        }
        return Result.success(canDelete)
    }

}