package com.portes.wikihikingosm.feature.hikings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portes.wikihikingosm.core.common.domain.None
import com.portes.wikihikingosm.core.domain.usecases.DeleteHikeByIdUseCase
import com.portes.wikihikingosm.core.domain.usecases.GetHikesUseCase
import com.portes.wikihikingosm.core.domain.usecases.HikingRoutePref
import com.portes.wikihikingosm.core.models.Hike
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HikingViewModel @Inject constructor(
    getHikesUseCase: GetHikesUseCase,
    private val deleteHikeByIdUseCase: DeleteHikeByIdUseCase,
    private val hikingRoutePref: HikingRoutePref
) : ViewModel() {

    val isStartHiking = flow {
        emit(hikingRoutePref.isStartHiking())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = 0,
    )

    val uiState: StateFlow<HikingUiState> = getHikesUseCase(None).map {
        HikingUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = HikingUiState.Loading,
        started = SharingStarted.WhileSubscribed()
    )

    fun deleteHike(idHike: Long) {
        viewModelScope.launch {
            deleteHikeByIdUseCase(DeleteHikeByIdUseCase.Params(idHike = idHike))
        }
    }

}

sealed interface HikingUiState {
    data class Success(
        val hikes: List<Hike>,
    ) : HikingUiState

    object Loading : HikingUiState
}