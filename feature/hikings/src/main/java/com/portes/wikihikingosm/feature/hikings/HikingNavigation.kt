package com.portes.wikihikingosm.feature.hikings

import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.portes.wikihikingosm.core.models.ImportHiking

const val HIKES_ROUTE = "HIKES"

fun NavGraphBuilder.hikeScreen(
    onClickHiking: (Long) -> Unit,
    importHiking: ImportHiking?,
    navigateToImport: (Uri?) -> Unit
) {
    composable(HIKES_ROUTE) {
        if (importHiking?.gpx == null) {
            HikingRoute(onClick = onClickHiking, onAddHike = navigateToImport)
        }

        LaunchedEffect(key1 = importHiking?.gpx) {
            if (importHiking?.gpx != null) {
                navigateToImport(null)
            }
        }
    }
}