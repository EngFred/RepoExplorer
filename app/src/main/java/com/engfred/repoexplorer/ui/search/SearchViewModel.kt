package com.engfred.repoexplorer.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.engfred.repoexplorer.domain.model.Repo
import com.engfred.repoexplorer.domain.usecase.SearchReposUseCase
import com.engfred.repoexplorer.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchReposUseCase: SearchReposUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val repoPagingFlow: Flow<PagingData<Repo>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            searchReposUseCase(query)
        }
        .cachedIn(viewModelScope)

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onToggleFavorite(repo: Repo) {
        viewModelScope.launch {
            toggleFavoriteUseCase(repo)
        }
    }
}