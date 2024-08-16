package com.portes.wikihikingosm.feature.imports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portes.wikihikingosm.core.domain.usecases.AddHikeUseCase
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ticofab.androidgpxparser.parser.domain.Gpx
import io.ticofab.androidgpxparser.parser.domain.TrackPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val addHikeUseCase: AddHikeUseCase,
) : ViewModel() {
    private val _hikingSaveUiState = MutableStateFlow<HikingSaveUiState>(HikingSaveUiState.Loading)
    val hikingSaveUiState: StateFlow<HikingSaveUiState> = _hikingSaveUiState.asStateFlow()

    fun addHike(gpx: Gpx) {
        gpx.let {
            viewModelScope.launch {
                addHikeUseCase(AddHikeUseCase.Params(getHike(it)))
                    .onSuccess {
                        _hikingSaveUiState.update { HikingSaveUiState.Success }
                    }.onFailure {
                        _hikingSaveUiState.update { HikingSaveUiState.Error }
                    }
            }
        }
    }

    private fun getHike(gpx: Gpx): Hike = Hike(
        name = gpx.tracks.firstOrNull()?.trackName.orEmpty(),
        startPoint = GeoPoint(
            gpx.getTrackPoints().firstOrNull()?.latitude ?: 0.0,
            gpx.getTrackPoints().firstOrNull()?.longitude ?: 0.0
        ),
        distanceTotal = getTotalDistance(gpx.getPoints()),
        route = mutableListOf<Route>().apply {
            gpx.getTrackPoints().map {
                add(
                    Route(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        elevation = it.elevation
                    )
                )
            }
        }
    )

    private fun getTotalDistance(list: List<GeoPoint>): Double =
        Polyline().apply { setPoints(list) }.distance
}

fun Gpx.getTrackPoints(): List<TrackPoint> =
    tracks.firstOrNull()?.trackSegments?.firstOrNull()?.trackPoints ?: emptyList()


fun Gpx.getPoints(): List<GeoPoint> = mutableListOf<GeoPoint>().apply {
    getTrackPoints().map {
        add(
            GeoPoint(it.latitude, it.longitude)
        )
    }
}

sealed interface HikingSaveUiState {
    object Success : HikingSaveUiState
    object Error : HikingSaveUiState
    object Loading : HikingSaveUiState
}