package com.portes.wikihikingosm.feature.routes.helpers

import android.content.res.Resources
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import com.portes.wikihikingosm.core.models.Route
import com.portes.wikihikingosm.core.models.WayPoints
import com.portes.wikihikingosm.core.designsystem.R
import com.portes.wikihikingosm.feature.routes.models.CustomMarker
import com.portes.wikihikingosm.feature.routes.models.ElevationMarker
import com.portes.wikihikingosm.feature.routes.models.WayPointMarker
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.lang.Exception
import javax.inject.Inject

class ConfigurationMapItems @Inject constructor(
    private val resources: Resources
) {

    private var mapView: MapView? = null

    fun initMap(mapView: MapView) {
        this.mapView = mapView
    }

    fun settingsMap(route: List<Route>) {
        if (mapView == null) {
            throw Exception("Invalid map")
        }

        val markerStart = Marker(mapView)
        markerStart.icon = ResourcesCompat.getDrawable(resources, R.drawable.start, null)
        markerStart.position = route.first().toGeoPoint()
        markerStart.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        markerStart.title = "Inicio"
        mapView?.overlays?.add(markerStart)

        val lineGo = Polyline()
        lineGo.setPoints(route.toListGeoPoint())
        mapView?.post {
            mapView?.zoomToBoundingBox(
                lineGo.bounds.increaseByScale(1.5f),
                true
            )
        }
    }


    fun addElevations(route: List<Route>) {
        var counter = 1
        val markers = ArrayList<CustomMarker>()
        route.forEachIndexed { index, _ ->
            if (counter < route.size) {
                val result = route[index].elevation - route[counter].elevation
                val icon = if (result > 0) {
                    R.drawable.happy
                } else {
                    R.drawable.sad
                }
                markers.add(
                    ElevationMarker(
                        position = route.toListGeoPoint()[counter],
                        icon = icon,
                        text = "${route[counter].elevation}"
                    )
                )
            }
            counter++
        }
        addMarkers(markers)
    }

    fun addWayPoints(wayPoints: List<WayPoints>) {
        val markers = ArrayList<CustomMarker>()

        wayPoints.map {
            markers.add(
                WayPointMarker(
                    position = it.toGeoPoint(),
                    icon = R.drawable.way_point,
                    text = it.name
                )
            )
        }
        addMarkers(markers)
    }

    private fun addMarkers(markers: ArrayList<CustomMarker>) {
        if (mapView == null) {
            throw Exception("Invalid map")
        }

        markers.map {
            val marker = Marker(mapView)
            marker.icon = ResourcesCompat.getDrawable(resources, it.icon, null)
            marker.position = it.position
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            marker.title = it.text
            mapView?.overlays?.add(marker)
        }
    }

    fun addRoute(route: List<GeoPoint>, color: Int) {
        if (mapView == null) {
            throw Exception("Invalid map")
        }

        val path = Polyline()
        path.outlinePaint.color = color
        path.outlinePaint.strokeWidth = 30f
        path.outlinePaint.strokeCap = Paint.Cap.ROUND
        path.outlinePaint.alpha = 255
        path.setPoints(route)
        mapView?.overlays?.add(path)
    }
}

fun Route.toGeoPoint() = GeoPoint(
    latitude,
    longitude
)

fun WayPoints.toGeoPoint() = GeoPoint(
    latitude,
    longitude
)

fun List<Route>.toListGeoPoint(): List<GeoPoint> = map {
    it.toGeoPoint()
}