package com.portes.wikihikingosm.feature.routes

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.portes.wikihikingosm.core.common.extensions.hasLocationPermissions
import com.portes.wikihikingosm.core.common.extensions.multiplePermissionsLauncher
import com.portes.wikihikingosm.core.common.extensions.toKm
import com.portes.wikihikingosm.core.common.extensions.viewBinding
import com.portes.wikihikingosm.core.domain.usecases.HikingRoutePref
import com.portes.wikihikingosm.core.models.Route
import com.portes.wikihikingosm.feature.routes.HikingRouteViewModel.Companion.ID_HIKE
import com.portes.wikihikingosm.feature.routes.databinding.ActivityHikingRouteBinding
import com.portes.wikihikingosm.feature.routes.helpers.ConfigurationMapItems
import com.portes.wikihikingosm.feature.routes.helpers.ConfigurationMapViewHelper
import com.portes.wikihikingosm.feature.routes.helpers.LocationOverlayHelper
import com.portes.wikihikingosm.feature.routes.helpers.MapEventsHelper
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
    lateinit var configurationMapItemsFactory: ConfigurationMapItems.Factory

    @Inject
    lateinit var hikingRoutePref: HikingRoutePref

    @Inject
    lateinit var locationOverlayHelperFactory: LocationOverlayHelper.Factory

    @Inject
    lateinit var mapEventsHelperFactory: MapEventsHelper.Factory

    @Inject
    lateinit var configurationMapViewHelper: ConfigurationMapViewHelper.Factory

    private var idHike = 0L
    private var backPressed = 0L
    private var configurationMapItems: ConfigurationMapItems? = null
    private var locationOverlayHelper: LocationOverlayHelper? = null

    companion object {
        private val ID_CALCULATE_DISTANCE_ROUTE = "CALCULATE_DISTANCE_ROUTE"
        private val ID_GO_ROUTE = "CALCULATE_GO_ROUTE"
        private val ID_GO_RETURN = "CALCULATE_GO_RETURN"
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBack()
            if (hikingRoutePref.isStartHiking() != 0L) {
                configurationMapItems?.removeRoute(ID_CALCULATE_DISTANCE_ROUTE)
                return
            }
            finish()
        }
    }

    private val requestPermissionLauncher =
        multiplePermissionsLauncher(allGranted = {
            locationOverlayHelper?.start()
        }, onReject = {})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance()
            .load(this, getSharedPreferences("map", Context.MODE_PRIVATE))
        setContentView(binding.root)
        configurationMapViewHelper.create(binding.contentMapView).start()
        configurationMapItems = configurationMapItemsFactory.create(binding.contentMapView)
        locationOverlayHelper = locationOverlayHelperFactory.create(binding.contentMapView)

        idHike = intent?.extras?.getLong(ID_HIKE) ?: 0

        if (hasLocationPermissions.not()) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            locationOverlayHelper?.start()
        }

        val mapEventsHelper = mapEventsHelperFactory.create(
            mapView = binding.contentMapView,
            configurationMapItems = configurationMapItems,
            locationOverlayHelper = locationOverlayHelper
        )

        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is HikingRouteUiState.Success -> configurationHikeRoute(
                        state.routeGo,
                        state.routeReturn
                    )

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
            locationOverlayHelper?.locationAnimation()
        } else {
            binding.startHikingButton.isVisible = true
            binding.locationFab.isVisible = false
            binding.stopHikingButton.isVisible = false
        }

        listeners()
        mapEventsHelper.startMapEvents(ID_CALCULATE_DISTANCE_ROUTE) { distance ->
            Toast.makeText(
                this@HikingRouteActivity,
                "Distancia ${distance.toKm()} km",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun listeners() {
        binding.locationFab.setOnClickListener {
            locationOverlayHelper?.locationAnimation()
        }

        binding.startHikingButton.setOnClickListener {
            hikingRoutePref.startHiking(idHike = idHike)
            binding.startHikingButton.isVisible = false
            binding.stopHikingButton.isVisible = true
            binding.locationFab.isVisible = true
            locationOverlayHelper?.locationAnimation()
        }
        binding.stopHikingButton.setOnClickListener {
            hikingRoutePref.startHiking(idHike = 0)
            finish()
        }
    }

    private fun configurationHikeRoute(routeGo: List<Route>, routeReturn: List<Route>) {
        configurationMapItems?.addRoute(
            route = routeGo.toListGeoPoint(),
            color = 0x009688,
            idRoute = ID_GO_ROUTE
        )
        configurationMapItems?.addRoute(
            route = routeReturn.toListGeoPoint(),
            color = 0xFFA000,
            idRoute = ID_GO_RETURN
        )

        val routeComplete = mutableListOf<Route>().apply {
            addAll(routeGo)
            addAll(routeReturn)
        }

        configurationMapItems?.addElevations(routeComplete)
        configurationMapItems?.settingsMap(routeComplete)
    }

    private fun onBack() {
        val totalRoute = configurationMapItems?.countRoute(ID_CALCULATE_DISTANCE_ROUTE) ?: 0
        if (totalRoute < 1) {
            if (backPressed + 2000 > System.currentTimeMillis()) {
                finishAffinity()
            } else {
                Toast.makeText(
                    this@HikingRouteActivity, "Presiona de nuevo para salir",
                    Toast.LENGTH_SHORT
                ).show()
                backPressed = System.currentTimeMillis();
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
}