package edu.dyds.movies.data.external.proxy

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.data.external.tmdb.RemoteTMDB
import edu.dyds.movies.data.external.tmdb.RemoteResult

class TMDBMoviesExternalSourceProxy(
    private val tmdbMoviesExternalSource: MovieExternalSource,
    private val tmdbMoviesListExternalSource: MoviesExternalSource,
) : MovieExternalSource {
    override suspend fun getMovieByTitle(title: String): RemoteTMDB? =
        tmdbMoviesExternalSource.getMovieByTitle(title)

    suspend fun getPopularMovies(): RemoteResult =
        tmdbMoviesListExternalSource.getPopularMovies()
}
