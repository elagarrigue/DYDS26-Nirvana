package edu.dyds.movies.data.repository

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.PopularMoviesExternalSource
import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val movieDetailExternalSource: MovieDetailExternalSource,
    private val popularMoviesExternalSource: PopularMoviesExternalSource,
    private val localDataSource: MoviesLocalDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cached = localDataSource.getPopularMoviesFromCache()
        if (cached.isNotEmpty()) return cached

        val remoteMovies = popularMoviesExternalSource.getPopularMovies()
        localDataSource.savePopularMovies(remoteMovies)
        return remoteMovies
    }

    override suspend fun getMovieByTitle(title: String): Movie? {
        val remoteMovie = runCatching { movieDetailExternalSource.getMovieByTitle(title) }.getOrNull()
        if (remoteMovie != null) {
            return remoteMovie
        }

        return localDataSource.getMovieDetailFromCache(title)
    }
}
