package com.portes.wikihikingosm.feature.hikings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.portes.wikihikingosm.core.common.extensions.toKm
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.designsystem.R
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikingRoute(
    viewModel: HikingViewModel = hiltViewModel(),
    onClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val startingHiking by viewModel.isStartHiking.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Caminatas",
                    style = MaterialTheme.typography.headlineMedium,
                )
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) { paddingValues ->
        HikingRoute(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            uiState,
            startingHiking,
            onClick,
            onGoStartPoint = {
                val gmmIntentUri =
                    Uri.parse("google.navigation:q=${it.latitude},${it.longitude}&mode=d")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
            }
        )
    }
}

@Composable
fun HikingRoute(
    modifier: Modifier,
    uiState: HikingUiState,
    startingHiking: Long,
    onClick: (Long) -> Unit,
    onGoStartPoint: (GeoPoint) -> Unit
) {

    when (uiState) {
        HikingUiState.Loading -> {

        }

        is HikingUiState.Success -> {
            if (startingHiking != 0L) {
                onClick(startingHiking)
            }

            LazyColumn(modifier) {
                items(items = uiState.hikes, key = { it.idHike }) { hike ->
                    HikingItem(
                        hike = hike,
                        onClick = { onClick(hike.idHike) },
                        onGoStartPoint = onGoStartPoint
                    )
                }
            }
        }
    }
}

@Composable
fun HikingItem(hike: Hike, onClick: (Hike) -> Unit, onGoStartPoint: (GeoPoint) -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onClick(hike) }
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = hike.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Distancia ${hike.distanceTotal.toKm()} km",
            )
        }

        Row {
            IconButton(onClick = { onGoStartPoint(hike.startPoint) }) {
                Icon(
                    painter = painterResource(id = R.drawable.direction),
                    contentDescription = null
                )
            }
        }
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    )
}