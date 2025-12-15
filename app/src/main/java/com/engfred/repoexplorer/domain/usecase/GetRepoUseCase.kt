package com.engfred.repoexplorer.domain.usecase

import com.engfred.repoexplorer.domain.model.Repo
import com.engfred.repoexplorer.domain.repository.RepoRepository
import javax.inject.Inject

class GetRepoUseCase @Inject constructor(
    private val repository: RepoRepository
) {
    suspend operator fun invoke(id: Long): Result<Repo> {
        return try {
            val repo = repository.getRepo(id)
            Result.success(repo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}