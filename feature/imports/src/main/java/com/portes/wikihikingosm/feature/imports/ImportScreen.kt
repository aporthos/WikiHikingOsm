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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.portes.wikihikingosm.core.models.ImportHiking
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun ImportRoute(
    viewModel: ImportViewModel = hiltViewModel(),
    importHiking: ImportHiking?,
    navigationToHome: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        importHiking?.gpx?.let {
            viewModel.addHike(it)
        }

        viewModel.hikingSaveUiState.onEach { hikingSaveUiState ->
            when (hikingSaveUiState) {
                HikingSaveUiState.Success -> {
                    Toast.makeText(context, "Importacion correcta :)", Toast.LENGTH_SHORT).show()
                    navigationToHome()
                }

                HikingSaveUiState.Error -> {
                    Toast.makeText(context, "Caminata repetida :(", Toast.LENGTH_SHORT).show()
                    navigationToHome()
                }
            }
        }.collect()
    }
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
        Text(text = "Importando los datos de tu caminata")
    }
}