package com.engfred.repoexplorer.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engfred.repoexplorer.domain.model.Repo
import com.engfred.repoexplorer.domain.usecase.GetRepoUseCase
import com.engfred.repoexplorer.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getRepoUseCase: GetRepoUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repoId: Long = checkNotNull(savedStateHandle["repoId"])

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        loadRepo()
    }

    fun loadRepo() {
        viewModelScope.launch {
            _uiState.value = DetailsUiState.Loading
            getRepoUseCase(repoId)
                .onSuccess { repo ->
                    _uiState.value = DetailsUiState.Success(repo)
                }
                .onFailure { error ->
                    _uiState.value = DetailsUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun onToggleFavorite() {
        val currentState = _uiState.value
        if (currentState is DetailsUiState.Success) {
            viewModelScope.launch {
                val currentRepo = currentState.repo
                toggleFavoriteUseCase(currentRepo)
                val updatedRepo = currentRepo.copy(isFavorite = !currentRepo.isFavorite)
                _uiState.value = DetailsUiState.Success(updatedRepo)
            }
        }
    }
}

sealed class DetailsUiState {
    data object Loading : DetailsUiState()
    data class Success(val repo: Repo) : DetailsUiState()
    data class Error(val message: String) : DetailsUiState()
}