package edu.dyds.movies.data.external.proxy

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.domain.entity.Movie

class TMDBMoviesExternalSourceProxy(
    private val tmdbMoviesExternalSource: MovieExternalSource,
    private val tmdbMoviesListExternalSource: MoviesExternalSource,
) : MovieExternalSource {
    override suspend fun getMovieByTitle(title: String): Movie? =
        tmdbMoviesExternalSource.getMovieByTitle(title)

    suspend fun getPopularMovies(): List<Movie> =
        tmdbMoviesListExternalSource.getPopularMovies()
}
