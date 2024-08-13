package com.portes.wikihikingosm.feature.routes

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.portes.wikihikingosm.core.common.extensions.hasLocationPermissions
import com.portes.wikihikingosm.core.common.extensions.multiplePermissionsLauncher
import com.portes.wikihikingosm.core.common.extensions.viewBinding
import com.portes.wikihikingosm.core.models.Route
import com.portes.wikihikingosm.feature.routes.databinding.ActivityHikingRouteBinding
import com.portes.wikihikingosm.core.domain.usecases.HikingRoutePref
import com.portes.wikihikingosm.feature.routes.HikingRouteViewModel.Companion.ID_HIKE
import com.portes.wikihikingosm.feature.routes.helpers.ConfigurationMapItems
import com.portes.wikihikingosm.feature.routes.helpers.ConfigurationMapViewHelper
import com.portes.wikihikingosm.feature.routes.helpers.LocationOverlayHelper
import com.portes.wikihikingosm.feature.routes.helpers.toListGeoPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HikingRouteActivity : AppCompatActivity() {

    private val viewModel: HikingRouteViewModel by viewModels()
    private val binding by viewBinding(ActivityHikingRouteBinding::inflate)

    @Inject
    lateinit var configurationMapItems: ConfigurationMapItems

    @Inject
    lateinit var hikingRoutePref: HikingRoutePref

    private var locationOverlayHelper: LocationOverlayHelper? = null
    private var idHike = 0L

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (hikingRoutePref.isStartHiking() != 0L) {
                return
            }
            finish()
        }
    }

    private val requestPermissionLauncher =
        multiplePermissionsLauncher(allGranted = {
            locationOverlayHelper =
                LocationOverlayHelper(
                    this,
                    binding.contentMapView
                )
        }, onReject = {})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance()
            .load(this, getSharedPreferences("map", Context.MODE_PRIVATE))
        setContentView(binding.root)

        ConfigurationMapViewHelper(binding.contentMapView)
        configurationMapItems.initMap(binding.contentMapView)
        idHike = intent?.extras?.getLong(ID_HIKE) ?: 0

        if (hasLocationPermissions.not()) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            locationOverlayHelper =
                LocationOverlayHelper(
                    this,
                    binding.contentMapView
                )
        }

        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is HikingRouteUiState.Success -> {
                        configurationHikeRoute(state.route)
                        configurationMapItems.addWayPoints(state.wayPoints)
                    }

                    HikingRouteUiState.Loading -> {
                        Timber.i("cargando ")
                    }
                }
            }
        }

        if (hikingRoutePref.isStartHiking() != 0L) {
            binding.startHikingButton.isVisible = false
            binding.stopHikingButton.isVisible = true
            binding.locationFab.isVisible = true
        } else {
            binding.startHikingButton.isVisible = true
            binding.locationFab.isVisible = false
            binding.stopHikingButton.isVisible = false
        }

        listeners()
    }

    private fun listeners() {
        binding.locationFab.setOnClickListener {
            val mapController = binding.contentMapView.controller
            val location = locationOverlayHelper?.myLocation
            locationOverlayHelper?.runOnFirstFix {
                lifecycleScope.launch {
                    mapController.setCenter(location)
                    mapController.animateTo(location)
                    mapController.setZoom(2.0)
                }
            }
        }

        binding.startHikingButton.setOnClickListener {
            hikingRoutePref.startHiking(idHike = idHike)
            binding.startHikingButton.isVisible = false
            binding.stopHikingButton.isVisible = true
            binding.locationFab.isVisible = true
        }
        binding.stopHikingButton.setOnClickListener {
            hikingRoutePref.startHiking(idHike = 0)
            finish()
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
}