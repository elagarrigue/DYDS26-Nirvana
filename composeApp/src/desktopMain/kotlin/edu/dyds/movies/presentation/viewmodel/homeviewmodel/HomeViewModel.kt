package edu.dyds.movies.presentation.viewmodel.homeviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.dyds.movies.domain.usecase.MoviesUseCases
import edu.dyds.movies.presentation.view.home.MoviesUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val moviesUseCases: MoviesUseCases
) : ViewModel() {

    private val moviesStateMutableStateFlow = MutableStateFlow(MoviesUiState())
    val moviesStateFlow: Flow<MoviesUiState> = moviesStateMutableStateFlow

    fun getAllMovies() {
        viewModelScope.launch {
            moviesStateMutableStateFlow.emit(
                MoviesUiState(isLoading = true)
            )
            val movies = moviesUseCases.getPopularMovies()
            moviesStateMutableStateFlow.emit(
                MoviesUiState(
                    isLoading = false,
                    movies = movies
                )
            )
        }
    }
}