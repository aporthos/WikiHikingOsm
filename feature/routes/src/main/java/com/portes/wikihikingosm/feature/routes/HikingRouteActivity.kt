package com.portes.wikihikingosm.feature.routes

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.portes.wikihikingosm.core.common.extensions.hasLocationPermissions
import com.portes.wikihikingosm.core.common.extensions.multiplePermissionsLauncher
import com.portes.wikihikingosm.core.common.extensions.toKm
import com.portes.wikihikingosm.core.common.extensions.toMeters
import com.portes.wikihikingosm.core.common.extensions.viewBinding
import com.portes.wikihikingosm.core.common.setImage
import com.portes.wikihikingosm.core.domain.usecases.HikingRoutePref
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.Route
import com.portes.wikihikingosm.feature.routes.HikingRouteViewModel.Companion.ID_HIKE
import com.portes.wikihikingosm.feature.routes.databinding.ActivityHikingRouteBinding
import com.portes.wikihikingosm.feature.routes.helpers.BottomSheetCallbackHelper
import com.portes.wikihikingosm.feature.routes.helpers.ConfigurationMapItems
import com.portes.wikihikingosm.feature.routes.helpers.ConfigurationMapViewHelper
import com.portes.wikihikingosm.feature.routes.helpers.LocationOverlayHelper
import com.portes.wikihikingosm.feature.routes.helpers.MapEventsHelper
import com.portes.wikihikingosm.feature.routes.helpers.MapListenerHelper
import com.portes.wikihikingosm.feature.routes.helpers.OnBackPressedCallbackHelper
import com.portes.wikihikingosm.feature.routes.helpers.toListGeoPoint
import com.portes.wikihikingosm.core.designsystem.R as DesignSystem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HikingRouteActivity : AppCompatActivity() {

    companion object {
        const val ID_CALCULATE_DISTANCE_ROUTE = "CALCULATE_DISTANCE_ROUTE"
        const val ID_GO_ROUTE = "CALCULATE_GO_ROUTE"
        const val ID_GO_RETURN = "CALCULATE_GO_RETURN"
    }

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
    private var configurationMapItems: ConfigurationMapItems? = null
    private var locationOverlayHelper: LocationOverlayHelper? = null
    private lateinit var modalBottomSheetRouteBehavior: BottomSheetBehavior<*>
    private var isScrollingMap = false
    private var isZoomMap = false

    private val requestPermissionLauncher =
        multiplePermissionsLauncher(allGranted = {
            locationOverlayHelper?.start()
        }, onReject = {})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarTransparent()
        Configuration.getInstance()
            .load(this, getSharedPreferences("map", Context.MODE_PRIVATE))

        setContentView(binding.root)
        idHike = intent?.extras?.getLong(ID_HIKE) ?: 0

        checkPermissions()
        with(binding) {
            configurationMapViewHelper.create(contentMapView).start()
            configurationMapItems = configurationMapItemsFactory.create(contentMapView)
            locationOverlayHelper = locationOverlayHelperFactory.create(contentMapView)
            modalBottomSheetRouteBehavior =
                BottomSheetBehavior.from(modalBottomSheetRoute.bottomSheetInfoRoute)
        }

        locationOverlayHelper?.start()
        mapEventsHelperFactory.create(
            mapView = binding.contentMapView,
            configurationMapItems = configurationMapItems,
            locationOverlayHelper = locationOverlayHelper
        ).startMapEvents(ID_CALCULATE_DISTANCE_ROUTE) { distance ->
            if (modalBottomSheetRouteBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                modalBottomSheetRouteBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            Toast.makeText(
                this@HikingRouteActivity,
                "Distancia ${distance.toKm()}",
                Toast.LENGTH_SHORT
            ).show()
        }

        onBackPressedDispatcher.addCallback(
            this, OnBackPressedCallbackHelper(context = this,
                hikingRoutePref = hikingRoutePref,
                configurationMapItems = configurationMapItems,
                onFinish = { finish() },
                onFinishAffinity = { finishAffinity() })
        )
        binding.contentMapView.addMapListener(
            MapListenerHelper(binding = binding, isScrollingMap = { isScrollingMap ->
                this.isScrollingMap = isScrollingMap
                if (isScrollingMap && modalBottomSheetRouteBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    modalBottomSheetRouteBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }, isZoomMap = { isZoomMap ->
                this.isZoomMap = isZoomMap
            })
        )

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is HikingRouteUiState.Success -> configurationHikeRoute(
                        state.hike,
                        state.routeGo,
                        state.routeReturn
                    )

                    HikingRouteUiState.Loading -> {
                        Timber.i("cargando ")
                    }
                }
            }
        }

        with(binding) {
            if (hikingRoutePref.isStartHiking() != 0L) {
                modalBottomSheetRoute.startHikingButton.isVisible = false
                modalBottomSheetRoute.imageCancelRoute.isVisible = true
                cardInfoMain.isVisible = true
                modalBottomSheetRouteBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                modalBottomSheetRoute.startHikingButton.isVisible = true
                modalBottomSheetRoute.imageCancelRoute.isVisible = false
                cardInfoMain.isVisible = false
                modalBottomSheetRouteBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        listeners()
    }

    private fun listeners() {
        binding.locationFab.setOnClickListener {
            locationOverlayHelper?.locationAnimation { elevation ->
                binding.elevationLabel.text = "Elevacion: ${elevation?.toMeters()}"
            }
        }

        with(binding.modalBottomSheetRoute) {
            startHikingButton.setOnClickListener {
                hikingRoutePref.startHiking(idHike = idHike)
                startHikingButton.isVisible = false
                imageCancelRoute.isVisible = true
                binding.locationFab.isVisible = true
                onStartLocation()
                if (modalBottomSheetRouteBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    modalBottomSheetRouteBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
            imageCancelRoute.setOnClickListener {
                hikingRoutePref.startHiking(idHike = 0)
                finish()
            }

            bottomSheetInfoRoute.setOnClickListener {
                if (modalBottomSheetRouteBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    modalBottomSheetRouteBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    modalBottomSheetRouteBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            imageShowInfoRoute.setOnClickListener {
                if (modalBottomSheetRouteBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    imageShowInfoRoute.setImage(
                        this@HikingRouteActivity,
                        DesignSystem.drawable.arrow_down
                    )
                    modalBottomSheetRouteBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    imageShowInfoRoute.setImage(
                        this@HikingRouteActivity,
                        DesignSystem.drawable.arrow_up
                    )
                    modalBottomSheetRouteBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            modalBottomSheetRouteBehavior.addBottomSheetCallback(
                BottomSheetCallbackHelper(
                    this@HikingRouteActivity,
                    binding.modalBottomSheetRoute
                )
            )
        }
    }

    private fun configurationHikeRoute(hike: Hike, routeGo: List<Route>, routeReturn: List<Route>) {
        with(binding.modalBottomSheetRoute) {
            nameRouteLabel.text = hike.name
            maxElevationLabel.text = hike.maxElevation.toMeters()
            minElevationLabel.text = hike.minElevation.toMeters()
            timeRouteLabel.text = hike.timeDuration
            distanceLabel.text = hike.distanceTotal.toKm()
        }
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

    private fun onStartLocation() {
        binding.cardInfoMain.isVisible = true
        locationOverlayHelper?.onLocationChanged { geoPoint ->
            binding.elevationLabel.text = "Elevacion: ${geoPoint?.altitude?.toMeters()}"
            locationOverlayHelper?.locationAnimation(geoPoint, isScrollingMap, isZoomMap)
        }
    }

    private fun setToolbarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, true)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    private fun checkPermissions() {
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
    }

    override fun onResume() {
        super.onResume()
        binding.contentMapView.onResume()
        if (hikingRoutePref.isStartHiking() != 0L) {
            onStartLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.contentMapView.onPause()
        locationOverlayHelper?.stopLocation()
    }
}