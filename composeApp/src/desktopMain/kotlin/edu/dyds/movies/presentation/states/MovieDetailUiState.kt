package edu.dyds.movies.presentation.states

import edu.dyds.movies.domain.entity.Movie

data class MovieDetailUiState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
)