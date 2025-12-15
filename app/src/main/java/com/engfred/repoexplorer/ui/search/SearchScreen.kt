package com.engfred.repoexplorer.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.engfred.repoexplorer.ui.common.RepoItem

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToDetails: (Long) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val pagingItems = viewModel.repoPagingFlow.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .statusBarsPadding()
    ) {
        // Search Bar
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search repositories...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF58A6FF),
                unfocusedBorderColor = Color.Gray
            ),
            shape = MaterialTheme.shapes.medium
        )

        if (pagingItems.loadState.refresh is LoadState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF58A6FF))
            }
        }

        else if (pagingItems.itemCount == 0) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (query.isBlank()) {
                    Text(
                        text = "Type to search GitHub\nrepositories...",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )
                } else {
                    Text(
                        text = "No results found for '$query'",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(pagingItems.itemCount) { index ->
                    val repo = pagingItems[index]
                    if (repo != null) {
                        RepoItem(
                            repo = repo,
                            onItemClick = { onNavigateToDetails(it.id) },
                            onFavoriteClick = { viewModel.onToggleFavorite(it) }
                        )
                    }
                }

                if (pagingItems.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF58A6FF))
                        }
                    }
                }

                if (pagingItems.loadState.append is LoadState.Error) {
                    val e = pagingItems.loadState.append as LoadState.Error
                    item {
                        ErrorItem(message = e.error.localizedMessage ?: "Unknown Error")
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorItem(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Error: $message", color = Color.Red)
    }
}