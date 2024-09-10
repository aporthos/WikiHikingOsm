package com.portes.wikihikingosm.feature.routes.helpers

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.portes.wikihikingosm.core.common.di.MainDispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import timber.log.Timber

class LocationOverlayHelper @AssistedInject constructor(
    @Assisted private val mapView: MapView,
    @ApplicationContext val context: Context,
    @MainDispatcher private val dispatcher: CoroutineDispatcher,
) {

    @AssistedFactory
    interface Factory {
        fun create(mapView: MapView): LocationOverlayHelper
    }

    private val scope = CoroutineScope(dispatcher)
    private var locationOverlay: LocationOverlay? = null

    fun start() {
        val location = GpsMyLocationProvider(context)
        location.locationUpdateMinTime = LOCATION_UPDATE_TIME
        locationOverlay = LocationOverlay(mapView, scope, location)
    }

    fun locationAnimation(
        onSuccessLocation: (Double?) -> Unit = {}
    ) {
        mapView.overlays.map { location ->
            if (location is LocationOverlay) {
                mapView.overlays.remove(location)
            }
        }
        mapView.controller.let { controller ->
            locationOverlay?.locationAnimation(controller) { elevation ->
                mapView.overlays.add(locationOverlay)
                onSuccessLocation(elevation)
            }
        }
    }

    fun locationAnimation(
        location: GeoPoint,
        isScrollingMap: Boolean,
        isZoomMap: Boolean
    ) {
        var isFirstLocation = true
        mapView.overlays.map { locationOverlay ->
            if (locationOverlay is LocationOverlay) {
                isFirstLocation = false
                mapView.overlays.remove(locationOverlay)
            }
        }
        mapView.controller.let { controller ->
            locationOverlay?.locationAnimation(
                controller = controller,
                scope = scope,
                location = location,
                isScrollingMap = isScrollingMap,
                isZoomMap = isZoomMap,
                isFirstLocation = isFirstLocation
            )
            mapView.overlays.add(locationOverlay)
        }
    }

    fun stopLocation() {
        locationOverlay?.stopLocation()
    }

    fun onLocationChanged(onLocationChanged: (GeoPoint) -> Unit) {
        scope.launch {
            locationOverlay?.onLocationChanged {
                onLocationChanged(it)
            }
        }

    }

    fun getLocation(): GeoPoint? = locationOverlay?.getLocation()
}

class LocationOverlay(
    mapView: MapView,
    private val scope: CoroutineScope,
    gps: GpsMyLocationProvider
) :
    MyLocationNewOverlay(gps, mapView) {

    private var onLocationChanged: (GeoPoint) -> Unit = {}

    init {
        enableMyLocation()
//        enableFollowLocation()
        isOptionsMenuEnabled = true
        isDrawAccuracyEnabled = true

    }

    fun locationAnimation(
        controller: IMapController,
        onSuccessLocation: (Double?) -> Unit
    ) {
        val location = getLocation()
        runOnFirstFix {
            scope.launch {
                controller.animateTo(location)
                controller.setZoom(22.0)
                onSuccessLocation(location?.altitude)
            }
        }
    }

    fun onLocationChanged(onLocationChanged: (GeoPoint) -> Unit) {
        this.onLocationChanged = onLocationChanged
    }

    fun stopLocation() {
        myLocationProvider.stopLocationProvider()
    }

    override fun onLocationChanged(location: Location?, source: IMyLocationProvider?) {
        super.onLocationChanged(location, source)
        Timber.d("location ${location?.provider}")
        if (location?.provider == LocationManager.GPS_PROVIDER) {
            onLocationChanged(
                GeoPoint(location.latitude, location.longitude, location.altitude)
            )
        }

    }

    fun locationAnimation(
        controller: IMapController,
        scope: CoroutineScope,
        location: GeoPoint,
        isScrollingMap: Boolean,
        isZoomMap: Boolean,
        isFirstLocation: Boolean
    ) {
        runOnFirstFix {
            scope.launch {
                Timber.i("isScrollingMap $isScrollingMap isZoomMap $isZoomMap")
                if (isScrollingMap.not() && isZoomMap.not()) {
                    controller.animateTo(location)
                }

                if (isFirstLocation or isZoomMap.not()) {
                    controller.setZoom(ZOOM_MAP)
                }
            }
        }
    }

    fun getLocation(): GeoPoint? = myLocation
}