package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

class InMemoryMoviesLocalDataSource : InterfaceInMemoryMoviesLocalDataSource {
    private val movieCache: MutableList<Movie> = mutableListOf()

    override suspend fun savePopularMovies(movies: List<Movie>) {
        movieCache.clear()
        movieCache.addAll(movies)
    }

    override suspend fun getPopularMoviesFromCache(): List<Movie> = movieCache.toList()

    override suspend fun getMovieDetailFromCache(id: Int): Movie? =
        movieCache.find { it.id == id }
}
