package com.portes.wikihikingosm.feature.routes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portes.wikihikingosm.core.domain.usecases.GetHikeUseCase
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.Route
import com.portes.wikihikingosm.core.models.WayPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HikingRouteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getHikeUseCase: GetHikeUseCase,
) : ViewModel() {

    companion object {
        const val ID_HIKE = "ID_HIKE"
    }

    private val idHike: Long = savedStateHandle[ID_HIKE] ?: 0

    val uiState: StateFlow<HikingRouteUiState> = getHikeUseCase(idHike).map {
        HikingRouteUiState.Success(
            routeGo = it.routeGo,
            routeReturn = it.routeReturn,
            hike = it.hike,
            wayPoints = it.wayPoints
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = HikingRouteUiState.Loading,
        started = SharingStarted.WhileSubscribed()
    )
}

sealed interface HikingRouteUiState {
    data class Success(
        val routeGo: List<Route> = emptyList(),
        val routeReturn: List<Route> = emptyList(),
        val hike: Hike,
        val wayPoints: List<WayPoints>,
    ) : HikingRouteUiState

    object Loading : HikingRouteUiState
}
