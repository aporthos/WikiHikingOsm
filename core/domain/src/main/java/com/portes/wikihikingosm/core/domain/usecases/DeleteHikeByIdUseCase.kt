package com.portes.wikihikingosm.core.domain.usecases

import com.portes.wikihikingosm.core.common.di.IoDispatcher
import com.portes.wikihikingosm.core.common.domain.UseCase
import com.portes.wikihikingosm.core.data.repositories.HikeRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteHikeByIdUseCase @Inject constructor(
    private val repository: HikeRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : UseCase<DeleteHikeByIdUseCase.Params, Result<Boolean>>(dispatcher) {

    data class Params(
        val idHike: Long
    )

    override suspend fun execute(parameters: Params): Result<Boolean> =
        Result.success(repository.deleteHike(parameters.idHike))
}