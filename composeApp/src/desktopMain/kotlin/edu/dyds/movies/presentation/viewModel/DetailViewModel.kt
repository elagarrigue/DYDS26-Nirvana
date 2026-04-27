package edu.dyds.movies.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.dyds.movies.domain.usecase.MoviesUseCases
import edu.dyds.movies.presentation.detail.MovieDetailUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val moviesUseCases: MoviesUseCases,
) : ViewModel() {

    private val movieDetailStateMutableStateFlow = MutableStateFlow(MovieDetailUiState())
    val movieDetailStateFlow: Flow<MovieDetailUiState> = movieDetailStateMutableStateFlow
    fun getMovieDetail(id: Int) {
        viewModelScope.launch {
            movieDetailStateMutableStateFlow.emit(
                MovieDetailUiState(isLoading = true)
            )
            val movie = moviesUseCases.getMovieDetails(id)
            movieDetailStateMutableStateFlow.emit(
                MovieDetailUiState(
                    isLoading = false,
                    movie = movie
                )
            )
        }
    }
}