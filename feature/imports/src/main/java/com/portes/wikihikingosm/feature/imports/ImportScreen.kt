package com.portes.wikihikingosm.feature.imports

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
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
        HikingSaveUiState.Loading -> ImportHike()
        HikingSaveUiState.Success -> {
            Toast.makeText(context, "Importacion correcta", Toast.LENGTH_SHORT).show()
            navigationToHome()
        }

        HikingSaveUiState.Error -> {
            Toast.makeText(context, "Caminata repetida :)", Toast.LENGTH_SHORT).show()
            navigationToHome()
        }
    }
}


@Composable
fun ImportHike() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.importing))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )

        Row {
            LottieAnimation(
                modifier = Modifier
                    .size(250.dp),
                composition = composition,
                progress = { progress },
            )
        }
        Text(text = "Importando los datos de tu caminta")
    }
}