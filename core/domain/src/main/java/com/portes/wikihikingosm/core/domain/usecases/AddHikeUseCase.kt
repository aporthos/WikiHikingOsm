package com.portes.wikihikingosm.core.domain.usecases

import com.portes.wikihikingosm.core.common.di.IoDispatcher
import com.portes.wikihikingosm.core.common.domain.UseCase
import com.portes.wikihikingosm.core.data.repositories.HikeRepository
import com.portes.wikihikingosm.core.models.Hike
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddHikeUseCase @Inject constructor(
    private val repository: HikeRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : UseCase<AddHikeUseCase.Params, Long>(dispatcher) {

    data class Params(
        val hike: Hike
    )

    override suspend fun execute(parameters: Params): Long = repository.addHike(parameters.hike)
}