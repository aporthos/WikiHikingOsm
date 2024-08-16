package com.portes.wikihikingosm.feature.imports

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.portes.wikihikingosm.core.models.ImportHiking

@Composable
fun ImportRoute(
    viewModel: ImportViewModel = hiltViewModel(),
    importHiking: ImportHiking?,
    navigationToHome: () -> Unit
) {
    val hikingSaveUiState by viewModel.hikingSaveUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = importHiking?.gpx) {
        importHiking?.gpx?.let {
            viewModel.addHike(it)
        }
    }

    ImportScreen(hikingSaveUiState = hikingSaveUiState, navigationToHome = navigationToHome)
}

@Composable
fun ImportScreen(hikingSaveUiState: HikingSaveUiState, navigationToHome: () -> Unit) {
    val context = LocalContext.current
    when (hikingSaveUiState) {
        HikingSaveUiState.Loading -> {

        }

        HikingSaveUiState.Success -> {
            Toast.makeText(context, "Importacion correcta", Toast.LENGTH_SHORT).show()
            navigationToHome()
        }

        HikingSaveUiState.Error -> {
            Toast.makeText(context, "Ya esta guardado", Toast.LENGTH_SHORT).show()
            navigationToHome()
        }
    }
}