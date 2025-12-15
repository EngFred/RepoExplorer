package com.engfred.repoexplorer.domain.repository

import androidx.paging.PagingData
import com.engfred.repoexplorer.domain.model.Repo
import kotlinx.coroutines.flow.Flow

interface RepoRepository {
    // Search returns infinite scroll data
    fun searchRepos(query: String): Flow<PagingData<Repo>>

    // Favorites are local operations
    fun getFavoriteRepos(): Flow<List<Repo>>
    suspend fun toggleFavorite(repo: Repo)
    suspend fun isRepoFavorite(repoId: Long): Boolean

    suspend fun getRepo(id: Long): Repo
}