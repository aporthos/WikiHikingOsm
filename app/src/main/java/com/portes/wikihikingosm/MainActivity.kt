package com.portes.wikihikingosm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.portes.wikihiking.ui.theme.WikiHikingTheme
import com.portes.wikihikingosm.feature.hikings.HikingRoute
import com.portes.wikihikingosm.feature.routes.HikingRouteActivity
import com.portes.wikihikingosm.feature.routes.HikingRouteViewModel.Companion.ID_HIKE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            WikiHikingTheme {
                HikingRoute(onClick = { hike ->
                    val intent = Intent(context, HikingRouteActivity::class.java)
                    intent.putExtra(ID_HIKE, hike)
                    context.startActivity(intent)
                })
            }
        }
    }
}