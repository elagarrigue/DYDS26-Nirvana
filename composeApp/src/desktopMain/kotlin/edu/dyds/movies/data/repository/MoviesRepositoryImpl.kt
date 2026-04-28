package edu.dyds.movies.data.repository

import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.data.remote.MoviesRemoteDataSource
import edu.dyds.movies.data.remote.mapper.MovieMapper
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import java.io.IOException

class MoviesRepositoryImpl(
    private val remoteDataSource: MoviesRemoteDataSource,
    private val localDataSource: MoviesLocalDataSource,
    private val movieMapper: MovieMapper = MovieMapper()
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cached = localDataSource.getPopularMoviesFromCache()
        if (cached.isNotEmpty()) return cached

        val remoteMovies = remoteDataSource.getPopularMovies().results
        val domainMovies = remoteMovies.map(movieMapper::toDomainMovie)

        localDataSource.savePopularMovies(domainMovies)
        return domainMovies
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
