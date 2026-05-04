package edu.dyds.movies.presentation.detail

import edu.dyds.movies.domain.entity.Movie

data class MovieDetailUiState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
)