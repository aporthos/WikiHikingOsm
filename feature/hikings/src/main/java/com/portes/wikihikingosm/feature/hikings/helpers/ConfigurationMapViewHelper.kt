package com.portes.wikihikingosm.feature.hikings.helpers

import android.graphics.Rect
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class ConfigurationMapViewHelper(
    mapView: MapView
) {
    init {
        mapView.apply {
            val mRotationGestureOverlay = RotationGestureOverlay(this)
            mRotationGestureOverlay.isEnabled = true

            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            mapCenter
            getLocalVisibleRect(Rect())
            overlays.add(mRotationGestureOverlay)
        }
    }
}