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
            dao.deleteFavorite(entity)
        } else {
            dao.insertFavorite(entity)
        }
    }

    override suspend fun isRepoFavorite(repoId: Long): Boolean {
        return dao.isFavorite(repoId)
    }

    override suspend fun getRepo(id: Long): Repo {
        // Network First (Fresh Content), Fallback to Local (Offline Support)
        val isFavorite = dao.isFavorite(id)

        return try {
            Log.d(TAG, "Repo: Fetching fresh details from Network")
            val remoteDto = api.getRepo(id)
            remoteDto.toDomain().copy(isFavorite = isFavorite)
        } catch (e: Exception) {
            Log.w(TAG, "Repo: Network failed, checking local cache. Error: ${e.message}")
            val localFavorite = dao.getFavoriteById(id)
            if (localFavorite != null) {
                Log.d(TAG, "Repo: Found in Local Cache")
                localFavorite.toDomain()
            } else {
                throw e
            }
        }
    }
}