package com.engfred.repoexplorer.ui.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    repoId: Long,
    viewModel: DetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
    ) {
        // 1. Column for TopAppBar and main scrollable content
        Column(modifier = Modifier.fillMaxSize()) {

            // Custom TopAppBar implementation
            TopAppBar(
                title = { Text("Details", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF161B22),
                    titleContentColor = Color.White
                )
            )

            // Main Content Area
            when (val state = uiState) {
                is DetailsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 64.dp))
                }
                is DetailsUiState.Error -> {
                    Text(text = state.message, color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 64.dp))
                }
                is DetailsUiState.Success -> {
                    val repo = state.repo
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Header
                        AsyncImage(
                            model = repo.ownerAvatarUrl,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(repo.name, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(repo.fullName, color = Color.Gray)

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F6FEB)), // Link Blue
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.OpenInBrowser, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View on GitHub")
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 2. Stats Grid (2x2)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                StatBadge(Icons.Default.Visibility, "${repo.watchers}", "Watchers")
                                StatBadge(Icons.Default.BugReport, "${repo.openIssues}", "Issues")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                StatBadge(Icons.Default.ForkRight, "${repo.forks}", "Forks")
                                StatBadge(Icons.Default.Star, "${repo.stars}", "Stars")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Description
                        Text("About", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = repo.description ?: "No description provided.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFC9D1D9), // Light Gray
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        // Spacer for FAB visibility
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        // 2. FloatingActionButton
        if (uiState is DetailsUiState.Success) {
            val repo = (uiState as DetailsUiState.Success).repo
            LargeFloatingActionButton(
                onClick = { viewModel.onToggleFavorite() },
                containerColor = Color(0xFF238636),
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = if (repo.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Toggle Favorite",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun StatBadge(icon: ImageVector, value: String, label: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF21262D)),
        modifier = Modifier.width(150.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF8B949E))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = label, color = Color.Gray, fontSize = 12.sp)
        }
    }
}