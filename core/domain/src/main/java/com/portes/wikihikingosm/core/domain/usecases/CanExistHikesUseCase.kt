package com.portes.wikihikingosm.core.domain.usecases

import com.portes.wikihikingosm.core.common.di.IoDispatcher
import com.portes.wikihikingosm.core.common.domain.FlowSingleUseCase
import com.portes.wikihikingosm.core.common.domain.None
import com.portes.wikihikingosm.core.data.repositories.HikeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CanExistHikesUseCase @Inject constructor(
    private val repository: HikeRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FlowSingleUseCase<None, Int>(dispatcher) {

    override fun execute(params: None): Flow<Int> = repository.canHikes()

}