package com.portes.wikihikingosm

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.portes.wikihikingosm.databinding.FragmentHikingRouteBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline

class HikingRouteFragment : Fragment(R.layout.fragment_hiking_route) {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private var _binding: FragmentHikingRouteBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance()
            .load(context, context?.getSharedPreferences("map", Context.MODE_PRIVATE))
        _binding = FragmentHikingRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.contentMapView.setTileSource(TileSourceFactory.MAPNIK)
        val mapController = binding.contentMapView.controller
        val startPoint = GeoPoint(20.0051082, -98.7737946)

        mapController.setZoom(20.0)
        mapController.setCenter(startPoint)

        addPolyline()
    }

    override fun onResume() {
        super.onResume()
        binding.contentMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.contentMapView.onPause()
    }

    private fun addPolyline() {
        val geoPoints = ArrayList<GeoPoint>()
        geoPoints.add(GeoPoint(20.006505, -98.775829))
        geoPoints.add(GeoPoint(20.006462, -98.775860))
        geoPoints.add(GeoPoint(20.006419, -98.775880))
        val line = Polyline()
        line.setPoints(geoPoints);
        binding.contentMapView.overlays.add(line)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

//    private fun requestPermissionsIfNecessary(permissions: List<String>) {
//        val permissionsToRequest = ArrayList<String>();
//        permissions.forEach { permission ->
//        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted
//            permissionsToRequest.add(permission);
//        }
//    }
//        if (permissionsToRequest.size > 0) {
//            ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    permissionsToRequest.toArray(array),
//                    REQUEST_PERMISSIONS_REQUEST_CODE);
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}