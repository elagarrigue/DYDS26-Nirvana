package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie

interface MoviesUseCases {
    suspend fun getMovieDetails(id: Int): Movie?
    suspend fun getPopularMovies(): List<QualifiedMovie>
}

