package com.engfred.repoexplorer.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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

    val favoriteIds by viewModel.favoriteIds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .statusBarsPadding()
    ) {
        // Elevated Search Area
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            placeholder = { Text("Search GitHub...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF58A6FF),
                focusedBorderColor = Color(0xFF58A6FF),
                unfocusedBorderColor = Color(0xFF30363D),
                focusedContainerColor = Color(0xFF0D1117),
                unfocusedContainerColor = Color(0xFF0D1117)
            ),
            shape = MaterialTheme.shapes.small
        )

        if (pagingItems.loadState.refresh is LoadState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF58A6FF))
            }
        }
        else if (pagingItems.itemCount == 0 && pagingItems.loadState.refresh !is LoadState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF30363D)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (query.isBlank()) {
                        Text("Discover Repositories", color = Color.White, fontSize = 20.sp)
                        Text("Type to search...", color = Color.Gray)
                    } else {
                        Text("No results found", color = Color.White, fontSize = 18.sp)
                        Text("We couldn't find anything for '$query'", color = Color.Gray)
                    }
                }
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                items(pagingItems.itemCount) { index ->
                    val repo = pagingItems[index]
                    if (repo != null) {
                        // 2. MERGE LOGIC: Check if this repo's ID is in our live favorites set
                        val isActuallyFavorite = favoriteIds.contains(repo.id)

                        // 3. Create a copy with the correct state just for the UI
                        val displayRepo = repo.copy(isFavorite = isActuallyFavorite)

                        RepoItem(
                            repo = displayRepo,
                            onItemClick = { onNavigateToDetails(it.id) },
                            onFavoriteClick = { viewModel.onToggleFavorite(it) }
                        )
                    }
                }

                if (pagingItems.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF58A6FF), modifier = Modifier.size(24.dp))
                        }
                    }
                }

                if (pagingItems.loadState.append is LoadState.Error) {
                    item {
                        ErrorItem(message = "Could not load more results")
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorItem(message: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = Color(0xFFDA3633))
    }
}