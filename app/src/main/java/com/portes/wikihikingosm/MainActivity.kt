package com.portes.wikihikingosm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.portes.wikihiking.ui.theme.WikiHikingTheme
import com.portes.wikihikingosm.feature.hikings.HikingRoute
import com.portes.wikihikingosm.feature.hikings.HikingRouteActivity
import com.portes.wikihikingosm.feature.hikings.HikingRoutePref
import com.portes.wikihikingosm.feature.hikings.HikingRouteViewModel.Companion.ID_HIKE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var hikingRoutePref: HikingRoutePref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            WikiHikingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HikingRoute(onClick = { hike ->
                        val intent = Intent(context, HikingRouteActivity::class.java)
                        intent.putExtra(ID_HIKE, hike)
                        context.startActivity(intent)
                    })
                }
            }
        }
    }
}