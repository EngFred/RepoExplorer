package com.engfred.repoexplorer.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engfred.repoexplorer.domain.model.Repo
import com.engfred.repoexplorer.domain.usecase.GetFavoritesUseCase
import com.engfred.repoexplorer.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    val favorites: StateFlow<List<Repo>> = getFavoritesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onRemoveFavorite(repo: Repo) {
        viewModelScope.launch {
            toggleFavoriteUseCase(repo)
        }
    }
}