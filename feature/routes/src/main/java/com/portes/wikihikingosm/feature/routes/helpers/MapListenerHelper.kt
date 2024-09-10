package com.portes.wikihikingosm.feature.routes.helpers

import androidx.core.view.isVisible
import com.portes.wikihikingosm.feature.routes.databinding.ActivityHikingRouteBinding
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent

class MapListenerHelper(
    private val binding: ActivityHikingRouteBinding,
    private val isScrollingMap: (Boolean) -> Unit,
    private val isZoomMap: (Boolean) -> Unit
) : MapListener {
    override fun onScroll(event: ScrollEvent): Boolean {
        val scrolling = event.x != 0 && event.y != 0
        binding.locationFab.isVisible = scrolling
        isScrollingMap(scrolling)
        return true
    }

    override fun onZoom(event: ZoomEvent): Boolean {
        val zoom = event.zoomLevel != ZOOM_MAP
        binding.locationFab.isVisible = zoom
        isZoomMap(zoom)
        return true
    }
}