package com.portes.wikihikingosm.feature.hikings.helpers

import android.graphics.Rect
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

class ConfigurationMapViewHelper(
    mapView: MapView
) {
    init {
        mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            mapCenter
            getLocalVisibleRect(Rect())
        }
    }
}