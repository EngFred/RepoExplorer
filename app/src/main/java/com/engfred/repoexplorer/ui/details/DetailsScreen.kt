package com.engfred.repoexplorer.ui.details

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.engfred.repoexplorer.ui.common.formatNumber
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    repoId: Long,
    viewModel: DetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Bar
            LargeTopAppBar(
                title = {
                    if (uiState is DetailsUiState.Success) {
                        Text((uiState as DetailsUiState.Success).repo.name)
                    } else {
                        Text("Details")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D1117),
                    scrolledContainerColor = Color(0xFF0D1117),
                    titleContentColor = Color.White
                ),
                scrollBehavior = scrollBehavior
            )

            // Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (val state = uiState) {
                    is DetailsUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF58A6FF))
                        }
                    }
                    is DetailsUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = state.message, color = Color.Red)
                        }
                    }
                    is DetailsUiState.Success -> {
                        val repo = state.repo
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            // Header Info
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = repo.ownerAvatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = repo.fullName,
                                        color = Color.Gray,
                                        fontSize = 16.sp
                                    )
                                    if (repo.language != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = repo.language,
                                            color = Color(0xFF58A6FF),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Description
                            Text(
                                text = repo.description ?: "No description provided.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFFE6EDF3),
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Stats Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF161B22), shape = MaterialTheme.shapes.medium)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatItem(Icons.Default.Star, formatNumber(repo.stars), "Stars")
                                StatItem(Icons.Default.ForkRight, formatNumber(repo.forks), "Forks")
                                StatItem(Icons.Default.Visibility, formatNumber(repo.watchers), "Watching")
                                StatItem(Icons.Default.BugReport, formatNumber(repo.openIssues), "Issues")
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Action Button
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, repo.htmlUrl.toUri())
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = MaterialTheme.shapes.medium,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30363D))
                            ) {
                                Icon(Icons.Default.OpenInBrowser, null, tint = Color(0xFF58A6FF))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("View on GitHub", color = Color(0xFF58A6FF))
                            }

                            Spacer(modifier = Modifier.height(88.dp))
                        }
                    }
                }
            }
        }

        if (uiState is DetailsUiState.Success) {
            val repo = (uiState as DetailsUiState.Success).repo
            FloatingActionButton(
                onClick = { viewModel.onToggleFavorite() },
                containerColor = Color(0xFF238636),
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = if (repo.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Toggle Favorite"
                )
            }
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF8B949E))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
    }
}