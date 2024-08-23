package com.portes.wikihikingosm

import android.net.Uri
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
    onAddHike: (Uri) -> Unit,
) {
    var canImportHiking by rememberSaveable { mutableStateOf(importHiking?.gpx != null) }
    NavHost(
        navController = navController,
        startDestination = "HOME"
    ) {
        composable("HOME") {
            LaunchedEffect(key1 = importHiking) {
                if (canImportHiking) {
                    navController.navigate("SAVE_HIKING")
                    canImportHiking = false
                }
            }
            if (canImportHiking.not()) {
                HikingRoute(onClick = onClickHiking, onAddHike = {
                    onAddHike(it)
                    navController.navigate("SAVE_HIKING")
                    canImportHiking = false
                })
            }
        }
        composable("SAVE_HIKING") {
            ImportRoute(importHiking = importHiking, navigationToHome = {
                navController.navigate("HOME")
            })
        }
    }
}
