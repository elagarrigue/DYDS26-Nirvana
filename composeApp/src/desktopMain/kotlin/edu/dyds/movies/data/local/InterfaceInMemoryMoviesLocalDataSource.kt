package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

interface InterfaceInMemoryMoviesLocalDataSource {
    suspend fun savePopularMovies(movies: List<Movie>)

    suspend fun getPopularMoviesFromCache(): List<Movie>

    suspend fun getMovieDetailFromCache(id: Int): Movie?
}