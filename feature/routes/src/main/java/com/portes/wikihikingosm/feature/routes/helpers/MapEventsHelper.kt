package com.portes.wikihikingosm.feature.routes.helpers

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay

class MapEventsHelper @AssistedInject constructor(
    @Assisted private val mapView: MapView,
    @Assisted private val configurationMapItems: ConfigurationMapItems?,
    @Assisted private val locationOverlayHelper: LocationOverlayHelper?
) {

    @AssistedFactory
    interface Factory {
        fun create(
            mapView: MapView,
            configurationMapItems: ConfigurationMapItems?,
            locationOverlayHelper: LocationOverlayHelper?
        ): MapEventsHelper
    }

    fun startMapEvents(idRoute: String, onDistance: (Double) -> Unit) {
        mapView.overlays.add(
            MapEventsOverlay(
                MapEventsReceiverHelper(
                    locationOverlayHelper,
                    configurationMapItems,
                    idRoute,
                    onDistance
                )
            )
        )
    }
}

class MapEventsReceiverHelper(
    private val locationOverlayHelper: LocationOverlayHelper?,
    private val configurationMapItems: ConfigurationMapItems?,
    private val idRoute: String,
    private val onDistance: (Double) -> Unit
) : MapEventsReceiver {
    override fun singleTapConfirmedHelper(geoPoint: GeoPoint): Boolean {
        locationOverlayHelper?.getLocation()?.let { geoPointCurrent ->
            val distance = geoPointCurrent.distanceToAsDouble(geoPoint)
            val route = mutableListOf<GeoPoint>()
            route.add(geoPointCurrent)
            route.add(geoPoint)
            configurationMapItems?.removeRoute(idRoute)
            configurationMapItems?.addRoute(route, 0x009640, idRoute = idRoute)
            onDistance(distance)
        }
        return true
    }

    override fun longPressHelper(geoPoint: GeoPoint): Boolean = false

}