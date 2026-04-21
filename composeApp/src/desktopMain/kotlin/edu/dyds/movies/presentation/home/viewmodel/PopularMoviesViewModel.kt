package edu.dyds.movies.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.presentation.home.state.MoviesUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PopularMoviesViewModel(
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    private val moviesStateMutableStateFlow = MutableStateFlow(MoviesUiState())

    val moviesStateFlow: Flow<MoviesUiState> = moviesStateMutableStateFlow

    fun getAllMovies() {
        viewModelScope.launch {
            moviesStateMutableStateFlow.emit(
                MoviesUiState(isLoading = true)
            )
            val movies = moviesRepository.getPopularMovies()
            moviesStateMutableStateFlow.emit(
                MoviesUiState(
                    isLoading = false,
                    movies = movies
                )
            )
        }
    }
}
