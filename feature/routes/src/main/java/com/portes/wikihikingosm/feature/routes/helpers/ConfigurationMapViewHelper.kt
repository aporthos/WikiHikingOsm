package com.portes.wikihikingosm.feature.routes.helpers

import android.graphics.Rect
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class ConfigurationMapViewHelper @AssistedInject constructor(
    @Assisted private val mapView: MapView
) {

    @AssistedFactory
    interface Factory {
        fun create(mapView: MapView): ConfigurationMapViewHelper
    }

    fun start() {
        mapView.apply {
            val rotationGestureOverlay = RotationGestureOverlay(this)
            rotationGestureOverlay.isEnabled = true

            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            getLocalVisibleRect(Rect())
            overlays.add(rotationGestureOverlay)
        }
    }
}