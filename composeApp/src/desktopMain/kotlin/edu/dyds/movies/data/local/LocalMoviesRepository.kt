package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository

private const val MIN_VOTE_AVERAGE = 6.0

class LocalMoviesRepository(
    private val localDataSource: MoviesLocalDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<QualifiedMovie> {
        return try {
            val cachedMovies = localDataSource.getPopularMoviesFromCache()
            cachedMovies.map { movie ->
                QualifiedMovie(
                    movie = movie,
                    isGoodMovie = true // If cached, consider as good
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieDetail(id: Int): Movie? {
        return try {
            localDataSource.getMovieDetailFromCache(id)
        } catch (e: Exception) {
            null
        }
    }
}

