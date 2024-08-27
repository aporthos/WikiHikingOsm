package com.portes.wikihikingosm

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.portes.wikihikingosm.core.models.ImportHiking
import com.portes.wikihikingosm.feature.hikings.hikeScreen
import com.portes.wikihikingosm.feature.imports.importScreen
import com.portes.wikihikingosm.feature.weather.WeatherScreen
import com.portes.wikihikingosm.feature.weather.weatherScreen
import com.portes.wikihikingosm.navigation.MainSections


@Composable
fun MainNavGraph(
    modifier: Modifier,
    appState: MainAppState,
    onClickHiking: (Long) -> Unit,
    importHiking: ImportHiking?,
    onAddHike: (Uri) -> Unit,
    resetHike: () -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = appState.navController,
        startDestination = MainSections.HIKES.route
    ) {
        hikeScreen(
            onClickHiking = onClickHiking,
            importHiking = importHiking,
            navigateToImport = { uri ->
                uri?.let(onAddHike)
                appState.navigateToImport()
            }
        )

        importScreen(importHiking = importHiking, navigateToHome = {
            resetHike()
            appState.navigateBack()
        })

        weatherScreen()
    }
}
