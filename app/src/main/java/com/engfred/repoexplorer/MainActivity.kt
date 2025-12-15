package com.engfred.repoexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.engfred.repoexplorer.ui.details.DetailsScreen
import com.engfred.repoexplorer.ui.favorites.FavoritesScreen
import com.engfred.repoexplorer.ui.navigation.Screen
import com.engfred.repoexplorer.ui.search.SearchScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepoExplorerApp()
        }
    }
}

@Composable
fun RepoExplorerApp() {
    val navController = rememberNavController()
    val screens = listOf(Screen.Search, Screen.Favorites)

    Column(modifier = Modifier.fillMaxSize()) {

        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = Modifier.weight(1f)
        ) {
            composable(Screen.Search.route) {
                SearchScreen(onNavigateToDetails = { id ->
                    navController.navigate(Screen.Details.createRoute(id))
                })
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(onNavigateToDetails = { id ->
                    navController.navigate(Screen.Details.createRoute(id))
                })
            }
            composable(
                route = Screen.Details.route,
                arguments = listOf(navArgument("repoId") { type = NavType.LongType })
            ) { backStackEntry ->
                val repoId = backStackEntry.arguments?.getLong("repoId") ?: 0L
                DetailsScreen(repoId = repoId, onBack = { navController.popBackStack() })
            }
        }

        // 2. NavigationBar (The persistent bottom bar)
        NavigationBar(
            containerColor = Color(0xFF161B22),
            contentColor = Color.White
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            screens.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon!!, contentDescription = null) },
                    label = { Text(screen.title) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF1F6FEB),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}