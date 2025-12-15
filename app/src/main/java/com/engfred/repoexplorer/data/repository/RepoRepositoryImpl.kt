package com.engfred.repoexplorer.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.engfred.repoexplorer.data.local.RepoDao
import com.engfred.repoexplorer.data.mapper.toDomain
import com.engfred.repoexplorer.data.mapper.toEntity
import com.engfred.repoexplorer.data.remote.GithubApi
import com.engfred.repoexplorer.data.remote.GithubPagingSource
import com.engfred.repoexplorer.domain.model.Repo
import com.engfred.repoexplorer.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "RepoDebug"

class RepoRepositoryImpl @Inject constructor(
    private val api: GithubApi,
    private val dao: RepoDao
) : RepoRepository {

    override fun searchRepos(query: String): Flow<PagingData<Repo>> {
        Log.d(TAG, "Repo: Creating new Pager for query: '$query'")
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { GithubPagingSource(api, query) }
        ).flow
    }

    override fun getFavoriteRepos(): Flow<List<Repo>> {
        return dao.getAllFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun toggleFavorite(repo: Repo) {
        val entity = repo.toEntity()
        if (dao.isFavorite(repo.id)) {
            Log.d(TAG, "Repo: Removing favorite ${repo.name}")
            dao.deleteFavorite(entity)
        } else {
            Log.d(TAG, "Repo: Adding favorite ${repo.name}")
            dao.insertFavorite(entity)
        }
    }

    override suspend fun isRepoFavorite(repoId: Long): Boolean {
        return dao.isFavorite(repoId)
    }

    override suspend fun getRepo(id: Long): Repo {
        Log.d(TAG, "Repo: Fetching details for ID: $id")

        // 1. Try Local DB (Single Source of Truth for favorites)
        val localFavorite = dao.getFavoriteById(id)
        if (localFavorite != null) {
            Log.d(TAG, "Repo: Found in Local Cache")
            return localFavorite.toDomain()
        }

        // 2. Fallback to Network
        Log.d(TAG, "Repo: Fetching from Network")
        return api.getRepo(id).toDomain()
    }
}