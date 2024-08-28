package com.portes.wikihikingosm.feature.routes.helpers

import android.content.Context
import com.portes.wikihikingosm.core.common.di.MainDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.lang.Exception
import javax.inject.Inject


class LocationOverlayHelper @Inject constructor(
    @ApplicationContext val context: Context,
    @MainDispatcher private val dispatcher: CoroutineDispatcher,
) {

    private val scope = CoroutineScope(dispatcher)
    private var locationOverlay: LocationOverlay? = null

    private var mapView: MapView? = null

    fun initLocation(mapView: MapView) {
        this.mapView = mapView
    }

    fun startLocationOverlay() {
        mapView?.let {
            locationOverlay = LocationOverlay(context, it)
        } ?: run {
            throw Exception("Invalid map")
        }
    }

    fun addMyLocation() {
        mapView?.controller?.let { controller ->
            locationOverlay?.addMyLocation(controller, scope)
        }

        mapView?.overlays?.map {
            if (it is LocationOverlay) {
                mapView?.overlays?.remove(it)
            }
        }

        mapView?.overlays?.add(locationOverlay)
    }
}

class LocationOverlay(context: Context, mapView: MapView) :
    MyLocationNewOverlay(GpsMyLocationProvider(context), mapView) {

    init {
        enableMyLocation()
//        enableFollowLocation()
        isOptionsMenuEnabled = true
        isDrawAccuracyEnabled = true
    }

    fun addMyLocation(controller: IMapController, scope: CoroutineScope) {
        runOnFirstFix {
            scope.launch {
                controller.setCenter(myLocation)
                controller.animateTo(myLocation)
                controller.setZoom(24.0)
            }
        }
    }
}