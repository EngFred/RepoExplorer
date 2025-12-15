package com.engfred.repoexplorer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Search : Screen("search", "Search", Icons.Default.Search)
    data object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    data object Details : Screen("details/{repoId}", "Details") {
        fun createRoute(repoId: Long) = "details/$repoId"
    }
}