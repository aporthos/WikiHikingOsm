package com.portes.wikihikingosm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.portes.wikihikingosm.feature.imports.IMPORT_HIKING_ROUTE

@Composable
fun rememberMainAppState(
    navController: NavHostController = rememberNavController(),
) = remember(navController) { MainAppState(navController) }

@Stable
class MainAppState(
    val navController: NavHostController,
) {

    fun navigateBack() {
        navController.navigateUp()
    }

    fun navigateToBottomBarRoute(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
        }
    }

    val navigateToImport: () -> Unit = {
        navController.navigate(IMPORT_HIKING_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }
}