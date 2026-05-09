package edu.dyds.movies.commonFakes

import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.domain.entity.Movie

class FakeMoviesLocalDataSource : MoviesLocalDataSource {
    var cachedMovies: List<Movie> = emptyList()
    var cachedMovieDetail: Movie? = null
    var savedMovies: List<Movie>? = null
    var requestedMovieId: Int? = null

    override suspend fun savePopularMovies(movies: List<Movie>) {
        savedMovies = movies
    }

    override suspend fun getPopularMoviesFromCache(): List<Movie> {
        return cachedMovies
    }

    override suspend fun getMovieDetailFromCache(id: Int): Movie? {
        requestedMovieId = id
        return cachedMovieDetail
    }
}

