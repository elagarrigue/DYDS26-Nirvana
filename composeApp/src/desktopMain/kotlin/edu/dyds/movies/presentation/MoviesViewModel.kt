package edu.dyds.movies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    private val moviesStateMutableStateFlow = MutableStateFlow(MoviesUiState())

    private val movieDetailStateMutableStateFlow = MutableStateFlow(MovieDetailUiState())

    val moviesStateFlow: Flow<MoviesUiState> = moviesStateMutableStateFlow

    val movieDetailStateFlow: Flow<MovieDetailUiState> = movieDetailStateMutableStateFlow

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

    fun getMovieDetail(id: Int) {
        viewModelScope.launch {
            movieDetailStateMutableStateFlow.emit(
                MovieDetailUiState(isLoading = true)
            )
            val movie = moviesRepository.getMovieDetail(id)
            movieDetailStateMutableStateFlow.emit(
                MovieDetailUiState(
                    isLoading = false,
                    movie = movie
                )
            )
        }
    }

    data class MoviesUiState(
        val isLoading: Boolean = false,
        val movies: List<QualifiedMovie> = emptyList(),
    )

    data class MovieDetailUiState(
        val isLoading: Boolean = false,
        val movie: Movie? = null,
    )
}