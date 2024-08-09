package com.portes.wikihikingosm.feature.hikings

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.portes.wikihikingosm.core.common.extensions.hasLocationPermissions
import com.portes.wikihikingosm.core.common.extensions.multiplePermissionsLauncher
import com.portes.wikihikingosm.core.models.Route
import com.portes.wikihikingosm.feature.hikings.databinding.FragmentHikingRouteBinding
import com.portes.wikihikingosm.feature.hikings.helpers.ConfigurationMapItems
import com.portes.wikihikingosm.feature.hikings.helpers.ConfigurationMapViewHelper
import com.portes.wikihikingosm.feature.hikings.helpers.LocationOverlayHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HikingRouteFragment : Fragment(R.layout.fragment_hiking_route) {

    private val viewModel: HikingRouteViewModel by viewModels()

    @Inject
    lateinit var configurationMapItems: ConfigurationMapItems

    private val requestPermissionLauncher =
        multiplePermissionsLauncher(allGranted = {
            LocationOverlayHelper(requireContext(), binding.contentMapView)
        }, onReject = {})

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
        ConfigurationMapViewHelper(binding.contentMapView)
        configurationMapItems.initMap(binding.contentMapView)
        if (requireContext().hasLocationPermissions.not()) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            LocationOverlayHelper(requireContext(), binding.contentMapView)
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is HikingRouteUiState.Success -> {
                        Timber.i("route size ${state.route.size}")
                        Timber.i("route ${state.route}")
                        configurationHikeRoute(state.route)
                        configurationMapItems.addWayPoints(state.wayPoints)
                    }

                    HikingRouteUiState.Loading -> {
                        Timber.i("cargando ")
                    }
                }
            }
        }
    }

    private fun configurationHikeRoute(route: List<Route>) {
        val middle = route.toListGeoPoint().size / 2
        configurationMapItems.addRoute(route.toListGeoPoint().take(middle + 1), 0x009688)
        configurationMapItems.addRoute(route.toListGeoPoint().takeLast(middle + 1), 0xFFA000)
        configurationMapItems.addElevations(route)
        configurationMapItems.settingsMap(route)
    }


    override fun onResume() {
        super.onResume()
        binding.contentMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.contentMapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}