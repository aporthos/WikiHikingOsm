package com.portes.wikihikingosm.feature.routes.helpers

import android.content.Context
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
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

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
        locationOverlay = LocationOverlay(context, mapView)
    }

    fun locationAnimation() {
        mapView.controller?.let { controller ->
            locationOverlay?.locationAnimation(controller, scope)
        }

        mapView.overlays?.map { location ->
            if (location is LocationOverlay) {
                mapView.overlays?.remove(location)
            }
        }

        mapView.overlays?.add(locationOverlay)
    }

    fun getLocation(): GeoPoint? = locationOverlay?.getLocation()
}

class LocationOverlay(context: Context, mapView: MapView) :
    MyLocationNewOverlay(GpsMyLocationProvider(context), mapView) {

    init {
        enableMyLocation()
//        enableFollowLocation()
        isOptionsMenuEnabled = true
        isDrawAccuracyEnabled = true
    }

    fun locationAnimation(
        controller: IMapController,
        scope: CoroutineScope,
    ) {
        val location = getLocation()
        runOnFirstFix {
            scope.launch {
                controller.setCenter(location)
                controller.animateTo(location)
                controller.setZoom(24.0)
            }
        }
    }

    fun getLocation(): GeoPoint? = myLocation
}