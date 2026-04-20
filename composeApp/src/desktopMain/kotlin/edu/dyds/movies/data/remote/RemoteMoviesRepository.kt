package edu.dyds.movies.data.remote

import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.data.mapper.toDomainMovie
import edu.dyds.movies.data.mapper.toDomainMovies
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository

private const val MIN_VOTE_AVERAGE = 6.0

class RemoteMoviesRepository(
    private val remoteDataSource: MoviesRemoteDataSource,
    private val localDataSource: MoviesLocalDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<QualifiedMovie> {
        return try {
            val remoteMovies = remoteDataSource.getPopularMovies().results
            val domainMovies = remoteMovies.toDomainMovies()

            // Save to local cache
            localDataSource.savePopularMovies(domainMovies)

            // Sort and qualify
            domainMovies
                .sortedByDescending { movie ->
                    remoteMovies.find { it.id == movie.id }?.voteAverage ?: 0.0
                }
                .map { movie ->
                    val voteAverage = remoteMovies.find { it.id == movie.id }?.voteAverage ?: 0.0
                    QualifiedMovie(
                        movie = movie,
                        isGoodMovie = voteAverage >= MIN_VOTE_AVERAGE
                    )
                }
        } catch (e: Exception) {
            // Fallback to cached movies if remote fails
            val cachedMovies = localDataSource.getPopularMoviesFromCache()
            cachedMovies.map { movie ->
                QualifiedMovie(
                    movie = movie,
                    isGoodMovie = true // If cached, consider as good
                )
            }
        }
    }

    override suspend fun getMovieDetail(id: Int): Movie? {
        return try {
            remoteDataSource.getMovieDetails(id).toDomainMovie()
        } catch (e: Exception) {
            // Fallback to cached movie if remote fails
            localDataSource.getMovieDetailFromCache(id)
        }
    }
}

