package com.engfred.repoexplorer.domain.usecase

import androidx.paging.PagingData
import com.engfred.repoexplorer.domain.model.Repo
import com.engfred.repoexplorer.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchReposUseCase @Inject constructor(
    private val repository: RepoRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Repo>> {
        return repository.searchRepos(query)
    }
}