package com.portes.wikihikingosm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import com.portes.wikihiking.ui.theme.WikiHikingTheme
import com.portes.wikihikingosm.core.models.ImportHiking
import com.portes.wikihikingosm.feature.routes.HikingRouteActivity
import com.portes.wikihikingosm.feature.routes.HikingRouteViewModel.Companion.ID_HIKE
import com.portes.wikihikingosm.navigation.mainDestinations
import dagger.hilt.android.AndroidEntryPoint
import io.ticofab.androidgpxparser.parser.GPXParser
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var parser: GPXParser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val importHiking by remember { mutableStateOf(ImportHiking()) }
            if (intent?.action == Intent.ACTION_VIEW) {
                intent.data?.let { uri ->
                    contentResolver.openInputStream(uri)?.let { input ->
                        importHiking.gpx = parser.parse(input)
                    }
                }
            }

            val context = LocalContext.current
            val appState = rememberMainAppState()
            val backStackEntry by appState.navController.currentBackStackEntryAsState()

            WikiHikingTheme {
                Scaffold(
                    bottomBar = {
                        MainBottomAppBar(
                            navBackStackEntry = backStackEntry,
                            navigateToRoute = appState::navigateToBottomBarRoute
                        )
                    },
                    content = { padding ->
                        MainNavGraph(
                            modifier = Modifier.padding(padding),
                            appState = appState,
                            onClickHiking = { hike ->
                                val intent = Intent(context, HikingRouteActivity::class.java)
                                intent.putExtra(ID_HIKE, hike)
                                context.startActivity(intent)
                            },
                            importHiking = importHiking,
                            onAddHike = {
                                contentResolver.openInputStream(it)?.let { input ->
                                    importHiking.gpx = parser.parse(input)
                                }
                            },
                            resetHike = {
                                importHiking.gpx = null
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun MainBottomAppBar(
    navBackStackEntry: NavBackStackEntry?,
    navigateToRoute: (String) -> Unit,
) {
    BottomAppBar {
        mainDestinations.forEach { item ->
            val selected = item.route == navBackStackEntry?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = { navigateToRoute(item.route) },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = "")
                },
                label = {
                    Text(
                        text = item.title
                    )
                }
            )
        }
    }
}