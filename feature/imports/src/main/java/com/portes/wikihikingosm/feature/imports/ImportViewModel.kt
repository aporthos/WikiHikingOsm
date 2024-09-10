package com.portes.wikihikingosm.feature.imports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portes.wikihikingosm.core.common.TYPE_GO
import com.portes.wikihikingosm.core.common.TYPE_RETURN
import com.portes.wikihikingosm.core.domain.usecases.AddHikeUseCase
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ticofab.androidgpxparser.parser.domain.Gpx
import io.ticofab.androidgpxparser.parser.domain.TrackPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val addHikeUseCase: AddHikeUseCase,
) : ViewModel() {
    private val _hikingSaveUiState = MutableSharedFlow<HikingSaveUiState>(replay = 1)
    val hikingSaveUiState: SharedFlow<HikingSaveUiState> = _hikingSaveUiState.asSharedFlow()

    fun addHike(gpx: Gpx) {
        viewModelScope.launch {
            addHikeUseCase(AddHikeUseCase.Params(getHike(gpx)))
                .onSuccess {
                    delay(1_000)
                    _hikingSaveUiState.tryEmit(HikingSaveUiState.Success)
                }.onFailure {
                    _hikingSaveUiState.tryEmit(HikingSaveUiState.Error)
                }
        }
    }

    private fun getHike(gpx: Gpx): Hike {
        val points = gpx.getPoints()
        val (maxElevation, minElevation) = getElevations(points)
        return Hike(
            name = gpx.tracks.firstOrNull()?.trackName.orEmpty(),
            startPoint = GeoPoint(
                gpx.getTrackPoints().firstOrNull()?.latitude ?: 0.0,
                gpx.getTrackPoints().firstOrNull()?.longitude ?: 0.0
            ),
            distanceTotal = getTotalDistance(points),
            route = getRoute(points),
            timeDuration = gpx.getTime(),
            maxElevation = maxElevation,
            minElevation = minElevation
        )
    }

    private fun getTotalDistance(list: List<GeoPoint>): Double =
        Polyline().apply { setPoints(list) }.distance

    private fun getRoute(list: List<GeoPoint>): List<Route> {
        val item = getItemMiddle(list)
        val routeGo = list.slice(0..item).map {
            Route(
                latitude = it.latitude,
                longitude = it.longitude,
                elevation = it.altitude,
                type = TYPE_GO
            )
        }
        val routeReturn = list.slice(item..(list.size - 1)).map {
            Route(
                latitude = it.latitude,
                longitude = it.longitude,
                elevation = it.altitude,
                type = TYPE_RETURN
            )
        }
        return mutableListOf<Route>().apply {
            addAll(routeGo)
            addAll(routeReturn)
        }
    }

    private fun getElevations(list: List<GeoPoint>): Pair<Double, Double> =
        Pair(list.maxBy { it.altitude }.altitude, list.minBy { it.altitude }.altitude)

    private fun getItemMiddle(list: List<GeoPoint>): Int {
        var distanceTotal = 0.0
        val totalDistance = getTotalDistance(list)
        var counter = 0
        var item = 1
        list.forEachIndexed { _, geoPoint ->
            counter++
            if (counter < list.size) {
                distanceTotal += geoPoint.distanceToAsDouble(list[counter])
                if (distanceTotal < (totalDistance / 2)) {
                    item = counter
                }
            }
        }
        return item
    }
}

fun Gpx.getTrackPoints(): List<TrackPoint> =
    tracks.firstOrNull()?.trackSegments?.firstOrNull()?.trackPoints ?: emptyList()


fun Gpx.getPoints(): List<GeoPoint> = mutableListOf<GeoPoint>().apply {
    getTrackPoints().map {
        add(
            GeoPoint(it.latitude, it.longitude, it.elevation)
        )
    }
}

fun Gpx.getTime(): String {
    val startDate = getTrackPoints().firstOrNull()?.time?.millis ?: 0
    val endDate = getTrackPoints().lastOrNull()?.time?.millis ?: 0

    val diffDate = endDate.minus(startDate)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diffDate)
    val days = TimeUnit.SECONDS.toDays(seconds)
    val hours = TimeUnit.SECONDS.toHours(seconds)
    val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60

    return if (days < 1) {
        "$hours h $minutes min"
    } else {
        "$days d $hours h $minutes min"
    }
}

sealed interface HikingSaveUiState {
    object Success : HikingSaveUiState
    object Error : HikingSaveUiState
}