package edu.dyds.movies.data.repository

import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.data.external.tmdb.MovieMapper
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import java.io.IOException

class MoviesRepositoryImpl(
    private val movieExternalSource: MovieExternalSource,
    private val moviesExternalSource: MoviesExternalSource,
    private val localDataSource: MoviesLocalDataSource,
    private val movieMapper: MovieMapper = MovieMapper()
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cached = localDataSource.getPopularMoviesFromCache()
        if (cached.isNotEmpty()) return cached

        val remoteMovies = moviesExternalSource.getPopularMovies().results
        val domainMovies = remoteMovies.map(movieMapper::toDomainMovie)

        localDataSource.savePopularMovies(domainMovies)
        return domainMovies
    }

    override suspend fun getMovieByTitle(title: String): Movie? {
        return try {
            val remoteMovie = movieExternalSource.getMovieByTitle(title)
            movieMapper.toDomainMovie(remoteMovie)
        } catch (e: IOException) {
            localDataSource.getPopularMoviesFromCache()
                .find { it.title.equals(title, ignoreCase = true) }
        }
    }
}
