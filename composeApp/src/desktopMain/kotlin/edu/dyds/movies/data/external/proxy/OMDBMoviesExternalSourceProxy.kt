package edu.dyds.movies.data.external.proxy

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.tmdb.RemoteTMDB

class OMDBMoviesExternalSourceProxy(
    private val omdbMoviesExternalSource: MovieExternalSource,
) : MovieExternalSource {
    override suspend fun getMovieByTitle(title: String): RemoteTMDB? =
        omdbMoviesExternalSource.getMovieByTitle(title)
}
