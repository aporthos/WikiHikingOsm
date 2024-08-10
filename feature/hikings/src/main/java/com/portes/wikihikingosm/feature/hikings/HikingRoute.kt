package com.portes.wikihikingosm.feature.hikings

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.portes.wikihikingosm.core.models.Hike

@Composable
fun HikingRoute(
    viewModel: HikingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HikingRoute(uiState)
}

@Composable
fun HikingRoute(
    uiState: HikingUiState
) {
    val context = LocalContext.current
    when (uiState) {
        HikingUiState.Loading -> {

        }

        is HikingUiState.Success -> {
            LazyColumn {
                items(items = uiState.hikes, key = { it.idHike }) { hike ->
                    HikingItem(hike) {
                        val intent = Intent(context, HikingRouteActivity::class.java)
                        intent.putExtra("ID_HIKE", hike.idHike)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun HikingItem(hike: Hike, onClick: (Hike) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onClick(hike)
            }
    ) {
        Text(
            text = hike.name
        )
    }
}