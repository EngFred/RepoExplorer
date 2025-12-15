package com.engfred.repoexplorer.domain.usecase

import com.engfred.repoexplorer.domain.model.Repo
import com.engfred.repoexplorer.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: RepoRepository
) {
    operator fun invoke(): Flow<List<Repo>> = repository.getFavoriteRepos()
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: RepoRepository
) {
    suspend operator fun invoke(repo: Repo) = repository.toggleFavorite(repo)
}

class CheckFavoriteUseCase @Inject constructor(
    private val repository: RepoRepository
) {
    suspend operator fun invoke(id: Long): Boolean = repository.isRepoFavorite(id)
}