package com.engfred.repoexplorer.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.engfred.repoexplorer.domain.model.Repo

@Composable
fun RepoItem(
    repo: Repo,
    onItemClick: (Repo) -> Unit,
    onFavoriteClick: (Repo) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick(repo) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                AsyncImage(
                    model = repo.ownerAvatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Owner Name
                Text(
                    text = repo.fullName.split("/").firstOrNull() ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Repo Name & Favorite Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF58A6FF)
                )
                IconButton(onClick = { onFavoriteClick(repo) }) {
                    Icon(
                        imageVector = if (repo.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (repo.isFavorite) Color.Red else Color.Gray
                    )
                }
            }

            // Description
            if (!repo.description.isNullOrEmpty()) {
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFC9D1D9),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Stats Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language
                if (repo.language != null) {
                    RepoStat(icon = Icons.Default.Circle, text = repo.language, color = Color(0xFFF1E05A)) // Kotlin Yellowish
                    Spacer(modifier = Modifier.width(16.dp))
                }
                // Stars
                RepoStat(icon = Icons.Default.Star, text = "${repo.stars}", color = Color(0xFFE3B341))
                Spacer(modifier = Modifier.width(16.dp))
                // Forks
                RepoStat(icon = Icons.Default.ForkRight, text = "${repo.forks}", color = Color.Gray)
            }
        }
    }
}

@Composable
fun RepoStat(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}