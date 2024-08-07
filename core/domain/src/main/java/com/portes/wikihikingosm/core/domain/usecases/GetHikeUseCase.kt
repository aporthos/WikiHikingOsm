package com.portes.wikihikingosm.core.domain.usecases

import com.portes.wikihikingosm.core.common.di.IoDispatcher
import com.portes.wikihikingosm.core.common.domain.FlowSingleUseCase
import com.portes.wikihikingosm.core.common.domain.None
import com.portes.wikihikingosm.core.data.repositories.HikeRepository
import com.portes.wikihikingosm.core.models.Hike
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHikeUseCase @Inject constructor(
    private val repository: HikeRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FlowSingleUseCase<None, List<Hike>>(dispatcher) {

    override fun execute(params: None): Flow<List<Hike>> = repository.getHike()

}