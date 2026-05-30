package edu.dyds.movies.data.repository

import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val movieExternalSource: MovieExternalSource,
    private val moviesExternalSource: MoviesExternalSource,
    private val localDataSource: MoviesLocalDataSource,
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cached = localDataSource.getPopularMoviesFromCache()
        if (cached.isNotEmpty()) return cached

        val remoteMovies = moviesExternalSource.getPopularMovies()

        localDataSource.savePopularMovies(remoteMovies)
        return remoteMovies
    }

    override suspend fun getMovieByTitle(title: String): Movie? {
        val remoteMovie = runCatching { movieExternalSource.getMovieByTitle(title) }.getOrNull()
        if (remoteMovie != null) {
            return remoteMovie
        }

        return localDataSource.getPopularMoviesFromCache()
            .find { it.title.equals(title, ignoreCase = true) }
    }
}
