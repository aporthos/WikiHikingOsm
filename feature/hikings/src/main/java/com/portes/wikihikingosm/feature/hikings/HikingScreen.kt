package com.portes.wikihikingosm.feature.hikings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.portes.wikihikingosm.core.common.extensions.toKm
import com.portes.wikihikingosm.core.designsystem.R
import com.portes.wikihikingosm.core.models.Hike
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import kotlin.time.Duration.Companion.seconds

@Composable
fun HikingRoute(
    viewModel: HikingViewModel = hiltViewModel(),
    onClick: (Long) -> Unit,
    onAddHike: (Uri) -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val startingHiking by viewModel.isStartHiking.collectAsStateWithLifecycle()
    var isShowButtonAdd by remember { mutableStateOf(true) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    onAddHike(uri)
                }
            }
        }
    )

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
            if (isShowButtonAdd) {
                FloatingActionButton(onClick = { launcher.launch(openChooserFile()) }) {
                    Icon(Icons.Filled.Add, "")
                }
            }

        }
    ) { paddingValues ->
        HikingRoute(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            uiState,
            startingHiking,
            onClick,
            onGoStartPoint = {
                context.startActivity(openNavigation(geoPoint = it))
            },
            isShowButtonAdd = {
                isShowButtonAdd = !it
            },
            onDeleteHike = {
                viewModel.deleteHike(it)
            },
            onAddHike = {
                launcher.launch(openChooserFile())
            }
        )
    }
}

private fun openChooserFile(): Intent {
    val extraMimeTypes = arrayOf("application/octet-stream", "application/gpx+xml")
    return Intent(Intent.ACTION_GET_CONTENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/*"
        putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes)
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }
}

private fun openNavigation(geoPoint: GeoPoint): Intent {
    val uri = Uri.parse("google.navigation:q=${geoPoint.latitude},${geoPoint.longitude}&mode=d")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")
    return intent
}

@Composable
fun HikingRoute(
    modifier: Modifier,
    uiState: HikingUiState,
    startingHiking: Long,
    onClick: (Long) -> Unit,
    onGoStartPoint: (GeoPoint) -> Unit,
    isShowButtonAdd: (Boolean) -> Unit,
    onDeleteHike: (Long) -> Unit,
    onAddHike: () -> Unit,
) {

    when (uiState) {
        HikingUiState.Loading -> {

        }

        is HikingUiState.Success -> {
            if (startingHiking != 0L) {
                onClick(startingHiking)
            }

            isShowButtonAdd(uiState.hikes.isEmpty())
            HikingItems(
                modifier = modifier,
                hikes = uiState.hikes,
                onGoStartPoint = onGoStartPoint,
                onClick = onClick,
                onDeleteHike = onDeleteHike,
                onAddHike = onAddHike
            )
        }
    }
}

@Composable
fun HikingItems(
    modifier: Modifier,
    hikes: List<Hike>,
    onGoStartPoint: (GeoPoint) -> Unit,
    onClick: (Long) -> Unit,
    onDeleteHike: (Long) -> Unit,
    onAddHike: () -> Unit,
) {
    if (hikes.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(com.portes.wikihikingosm.feature.hikings.R.raw.empty_hike))
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
            Text(text = "Aun no tienes caminatas :(")
            Button(onClick = { onAddHike() }) {
                Text(text = "Agregar caminata")
            }
        }
    } else {
        LazyColumn(modifier) {
            items(items = hikes, key = { it.idHike }) { hike ->
                HikingItemSwipe(
                    hike = hike,
                    onGoStartPoint = onGoStartPoint,
                    onClick = onClick,
                    onDeleteHike = onDeleteHike
                )
            }
        }
    }
}

@Composable
fun HikingItemSwipe(
    hike: Hike,
    onGoStartPoint: (GeoPoint) -> Unit,
    onClick: (Long) -> Unit,
    onDeleteHike: (Long) -> Unit
) {
    var isDeleted by remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()

    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                isDeleted = true
                coroutine.launch {
                    delay(1.seconds)
                    onDeleteHike(hike.idHike)
                }
                true
            } else {
                false
            }
        },
        positionalThreshold = { it * .80f }
    )

    val color = if (state.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
        Color.Red
    } else {
        Color.Transparent
    }

    AnimatedVisibility(
        visible = !isDeleted,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 700),
            shrinkTowards = Alignment.Bottom
        )
    ) {

        SwipeToDismissBox(
            state = state,
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = true,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            text = "Eliminar ?"
                        )
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Filled.Delete,
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
            }, content = {
                HikingItem(
                    hike = hike,
                    onClick = { onClick(hike.idHike) },
                    onGoStartPoint = onGoStartPoint
                )
            })
    }
}

@Composable
fun HikingItem(hike: Hike, onClick: (Hike) -> Unit, onGoStartPoint: (GeoPoint) -> Unit) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
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
                    text = "Distancia ${hike.distanceTotal.toKm()}",
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
}