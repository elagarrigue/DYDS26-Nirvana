package edu.dyds.movies.data.external.proxy

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.domain.entity.Movie

class OMDBMoviesExternalSourceProxy(
    private val omdbMoviesExternalSource: MovieExternalSource,
) : MovieExternalSource {
    override suspend fun getMovieByTitle(title: String): Movie? =
        omdbMoviesExternalSource.getMovieByTitle(title)
}
