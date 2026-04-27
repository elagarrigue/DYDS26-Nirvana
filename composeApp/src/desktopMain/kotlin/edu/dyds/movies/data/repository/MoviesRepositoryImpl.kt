package edu.dyds.movies.data.repository

import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.data.remote.MoviesRemoteDataSource
import edu.dyds.movies.domain.mapper.MovieMapper
import edu.dyds.movies.domain.qualifier.MovieQualifier
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository
import java.io.IOException

class MoviesRepositoryImpl(
    private val remoteDataSource: MoviesRemoteDataSource,
    private val localDataSource: MoviesLocalDataSource,
    private val movieMapper: MovieMapper = MovieMapper(),
    private val movieQualifier: MovieQualifier = MovieQualifier
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<QualifiedMovie> {
        return try {
            fetchPopularMoviesFromRemote()
        } catch (e: IOException) {
            fetchPopularMoviesFromCache()
        }
    }

    private suspend fun fetchPopularMoviesFromRemote(): List<QualifiedMovie> {
        val remoteMovies = remoteDataSource.getPopularMovies().results
        val domainMovies = remoteMovies.map(movieMapper::toDomainMovie)

        localDataSource.savePopularMovies(domainMovies)

        return movieQualifier.buildQualifiedMovies(remoteMovies, domainMovies)
    }

    private suspend fun fetchPopularMoviesFromCache(): List<QualifiedMovie> {
        return localDataSource
            .getPopularMoviesFromCache()
            .map(movieQualifier::qualifyMovie)
    }

    override suspend fun getMovieDetail(id: Int): Movie? {
        return try {
            val remoteMovie = remoteDataSource.getMovieDetails(id)
            movieMapper.toDomainMovie(remoteMovie)
        } catch (e: IOException) {
            localDataSource.getMovieDetailFromCache(id)
        }
    }
}
