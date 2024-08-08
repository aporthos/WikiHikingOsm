package com.portes.wikihikingosm.feature.hikings

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.portes.wikihikingosm.feature.hikings.databinding.FragmentHikingRouteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Polyline
import timber.log.Timber

@AndroidEntryPoint
class HikingRouteFragment : Fragment(R.layout.fragment_hiking_route) {

    private val viewModel: HikingRouteViewModel by viewModels()

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private var _binding: FragmentHikingRouteBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance()
            .load(context, context?.getSharedPreferences("map", Context.MODE_PRIVATE))
        _binding = FragmentHikingRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.contentMapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.contentMapView.setMultiTouchControls(true)

//        viewModel.addRoute()

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is HikingRouteUiState.Success -> {
                        Timber.i("route name ${state.hike.name}")
                        Timber.i("route size ${state.route.size}")
                        Timber.i("route ${state.route}")

                        val mapController = binding.contentMapView.controller
                        val startPoint = state.route.first()

                        mapController.setZoom(20.0)
                        mapController.setCenter(startPoint)
                        val line = Polyline()
                        line.setPoints(state.route)
                        binding.contentMapView.overlays.add(line)
                    }

                    HikingRouteUiState.Loading -> {
                        Timber.i("cargando ")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.contentMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.contentMapView.onPause()
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