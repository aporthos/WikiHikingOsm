package com.portes.wikihikingosm.feature.routes.helpers

import android.content.Context
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class LocationOverlayHelper(
    context: Context,
    mapView: MapView
) : MyLocationNewOverlay(GpsMyLocationProvider(context), mapView) {

    init {
        enableMyLocation()
//        enableFollowLocation()
        isOptionsMenuEnabled = true
        isDrawAccuracyEnabled = true
        mapView.overlays.add(this)
    }
}