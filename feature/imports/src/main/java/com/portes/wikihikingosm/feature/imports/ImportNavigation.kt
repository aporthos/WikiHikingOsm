package com.portes.wikihikingosm.feature.imports

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.portes.wikihikingosm.core.models.ImportHiking

const val IMPORT_HIKING_ROUTE = "IMPORT_HIKING"

fun NavGraphBuilder.importScreen(
    importHiking: ImportHiking?,
    navigateToHome: () -> Unit
) {
    composable(IMPORT_HIKING_ROUTE) {
        ImportRoute(importHiking = importHiking, navigationToHome = {
            navigateToHome()
        })
    }
}
