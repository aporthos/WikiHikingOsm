package com.portes.wikihikingosm.core.common.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext


abstract class FlowUseCase<in P, R>(private val dispatcher: CoroutineDispatcher) {
    operator fun invoke(params: P): Flow<Result<R>> = execute(params).flowOn(dispatcher)

    protected abstract fun execute(params: P): Flow<Result<R>>
}

abstract class FlowSingleUseCase<in P, R>(private val dispatcher: CoroutineDispatcher) {
    operator fun invoke(params: P): Flow<R> = execute(params).flowOn(dispatcher)

    protected abstract fun execute(params: P): Flow<R>
}


abstract class UseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(params: P): R {
        return withContext(coroutineDispatcher) {
            execute(params)
        }
    }

    protected abstract suspend fun execute(parameters: P): R
}

object None