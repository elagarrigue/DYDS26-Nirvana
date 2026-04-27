package edu.dyds.movies.presentation.view.home

import edu.dyds.movies.domain.entity.QualifiedMovie

data class MoviesUiState(
    val isLoading: Boolean = false,
    val movies: List<QualifiedMovie> = emptyList(),
)