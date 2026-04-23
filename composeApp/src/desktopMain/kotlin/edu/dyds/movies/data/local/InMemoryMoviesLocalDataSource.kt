package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

class InMemoryMoviesLocalDataSource {
    private val movieCache: MutableList<Movie> = mutableListOf()

    suspend fun savePopularMovies(movies: List<Movie>) {
        movieCache.clear()
        movieCache.addAll(movies)
    }

    suspend fun getPopularMoviesFromCache(): List<Movie> = movieCache.toList()

    suspend fun getMovieDetailFromCache(id: Int): Movie? =
        movieCache.find { it.id == id }
}
