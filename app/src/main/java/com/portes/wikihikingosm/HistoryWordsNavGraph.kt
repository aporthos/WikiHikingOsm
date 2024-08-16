package com.portes.wikihikingosm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.portes.wikihikingosm.core.models.ImportHiking
import com.portes.wikihikingosm.feature.hikings.HikingRoute
import com.portes.wikihikingosm.feature.imports.ImportRoute

@Composable
fun HistoryWordsNavGraph(
    navController: NavHostController,
    onClickHiking: (Long) -> Unit,
    importHiking: ImportHiking?,
) {
    var showSettingsDialog by rememberSaveable { mutableStateOf(importHiking != null) }

    NavHost(
        navController = navController,
        startDestination = "HOME"
    ) {
        composable("HOME") {
            LaunchedEffect(key1 = showSettingsDialog) {
                if (showSettingsDialog) {
                    navController.navigate("SAVE_HIKING")
                    showSettingsDialog = false
                }
            }
            if (showSettingsDialog.not()) {
                HikingRoute(onClick = onClickHiking)
            }
        }
        composable("SAVE_HIKING") {
            ImportRoute(importHiking = importHiking, navigationToHome = {
                navController.navigate("HOME")
            })
        }
    }
}
