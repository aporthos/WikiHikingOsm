package com.portes.wikihikingosm.feature.hikings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portes.wikihikingosm.core.common.domain.None
import com.portes.wikihikingosm.core.domain.usecases.AddHikeUseCase
import com.portes.wikihikingosm.core.domain.usecases.CanExistHikesUseCase
import com.portes.wikihikingosm.core.domain.usecases.GetHikeUseCase
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.Route
import com.portes.wikihikingosm.core.models.WayPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HikingRouteViewModel @Inject constructor(
    private val addHikeUseCase: AddHikeUseCase,
    private val getHikeUseCase: GetHikeUseCase,
    private val canExistHikesUseCase: CanExistHikesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HikingRouteUiState>(HikingRouteUiState.Loading)
    val uiState: StateFlow<HikingRouteUiState> = _uiState

    init {
        addHike()
        viewModelScope.launch {
            canExistHikesUseCase(None).collect {
                if (it > 0) {
                    getHike()
                }
            }
        }

    }

    private fun addHike() {
        viewModelScope.launch {
            addHikeUseCase(AddHikeUseCase.Params(Hike("pena2.gpx")))
        }
    }

    private fun getHike() {
        viewModelScope.launch {
            getHikeUseCase(None).collect { hike ->
                _uiState.update {
                    HikingRouteUiState.Success(
                        hike = hike.hike,
                        route = hike.route,
                        wayPoints = hike.wayPoints
                    )
                }
            }
        }
    }
}

fun Route.toGeoPoint() = GeoPoint(
    latitude,
    longitude
)

fun WayPoints.toGeoPoint() = GeoPoint(
    latitude,
    longitude
)

fun List<Route>.toListGeoPoint(): List<GeoPoint> = map {
    it.toGeoPoint()
}

sealed interface HikingRouteUiState {
    data class Success(
        val route: List<Route> = emptyList(),
        val hike: Hike,
        val wayPoints: List<WayPoints>,
    ) : HikingRouteUiState

    object Loading : HikingRouteUiState
}
