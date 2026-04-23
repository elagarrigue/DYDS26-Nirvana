package edu.dyds.movies.data.repository

import edu.dyds.movies.data.local.InMemoryMoviesLocalDataSource
import edu.dyds.movies.data.remote.TmdbMoviesRemoteDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository

private const val MIN_VOTE_AVERAGE = 6.0

class MoviesRepositoryImpl(
    private val remoteDataSource: TmdbMoviesRemoteDataSource,
    private val localDataSource: InMemoryMoviesLocalDataSource
) : MoviesRepository {
    override suspend fun getPopularMovies(): List<QualifiedMovie> {
        return try {
            val remoteMovies = remoteDataSource.getPopularMovies().results
            val domainMovies = remoteMovies.map { remoteMovie ->
                Movie(
                    id = remoteMovie.id,
                    title = remoteMovie.title,
                    overview = remoteMovie.overview,
                    releaseDate = remoteMovie.releaseDate,
                    poster = "https://image.tmdb.org/t/p/w185${remoteMovie.posterPath}",
                    backdrop = remoteMovie.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
                    originalTitle = remoteMovie.originalTitle,
                    originalLanguage = remoteMovie.originalLanguage,
                    popularity = remoteMovie.popularity,
                    voteAverage = remoteMovie.voteAverage
                )
            }
            localDataSource.savePopularMovies(domainMovies)
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
            val cachedMovies = localDataSource.getPopularMoviesFromCache()
            cachedMovies.map { movie ->
                QualifiedMovie(
                    movie = movie,
                    isGoodMovie = true
                )
            }
        }
    }

    override suspend fun getMovieDetail(id: Int): Movie? {
        return try {
            val remoteMovie = remoteDataSource.getMovieDetails(id)
            Movie(
                id = remoteMovie.id,
                title = remoteMovie.title,
                overview = remoteMovie.overview,
                releaseDate = remoteMovie.releaseDate,
                poster = "https://image.tmdb.org/t/p/w185${remoteMovie.posterPath}",
                backdrop = remoteMovie.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
                originalTitle = remoteMovie.originalTitle,
                originalLanguage = remoteMovie.originalLanguage,
                popularity = remoteMovie.popularity,
                voteAverage = remoteMovie.voteAverage
            )
        } catch (e: Exception) {
            localDataSource.getMovieDetailFromCache(id)
        }
    }

    companion object {
        fun create(
            remoteDataSource: TmdbMoviesRemoteDataSource,
            localDataSource: InMemoryMoviesLocalDataSource
        ): MoviesRepositoryImpl = MoviesRepositoryImpl(remoteDataSource, localDataSource)
    }
}
