package com.portes.wikihikingosm.feature.routes.helpers

import android.content.Context
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.portes.wikihikingosm.core.domain.usecases.HikingRoutePref
import com.portes.wikihikingosm.feature.routes.HikingRouteActivity

class OnBackPressedCallbackHelper(
    private val context: Context,
    private val hikingRoutePref: HikingRoutePref,
    private val configurationMapItems: ConfigurationMapItems?,
    private val onFinish: () -> Unit,
    private val onFinishAffinity: () -> Unit
) : OnBackPressedCallback(true) {
    private var backPressed = 0L
    override fun handleOnBackPressed() {
        if (hikingRoutePref.isStartHiking() != 0L) {
            onBack()
            configurationMapItems?.removeRoute(HikingRouteActivity.ID_CALCULATE_DISTANCE_ROUTE)
            return
        }
        onFinish()
    }

    private fun onBack() {
        val totalRoute =
            configurationMapItems?.countRoute(HikingRouteActivity.ID_CALCULATE_DISTANCE_ROUTE) ?: 0
        if (totalRoute < 1) {
            if (backPressed + 2000 > System.currentTimeMillis()) {
                onFinishAffinity()
            } else {
                Toast.makeText(
                    context, "Presiona de nuevo para salir",
                    Toast.LENGTH_SHORT
                ).show()
                backPressed = System.currentTimeMillis();
            }
        }
    }
}