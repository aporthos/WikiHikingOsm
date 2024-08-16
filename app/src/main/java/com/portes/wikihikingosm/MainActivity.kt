package com.portes.wikihikingosm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.portes.wikihiking.ui.theme.WikiHikingTheme
import com.portes.wikihikingosm.feature.routes.HikingRouteActivity
import com.portes.wikihikingosm.feature.routes.HikingRouteViewModel.Companion.ID_HIKE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.let { uri ->
                contentResolver.openInputStream(uri)?.let { input ->
                    viewModel.setImport(inputStream = input)
                }
            }
        }

        setContent {
            val context = LocalContext.current
            WikiHikingTheme {
                HistoryWordsNavGraph(
                    navController = rememberNavController(),
                    onClickHiking = { hike ->
                        val intent = Intent(context, HikingRouteActivity::class.java)
                        intent.putExtra(ID_HIKE, hike)
                        context.startActivity(intent)
                    }, importHiking = viewModel.importHiking
                )
            }
        }
    }
}