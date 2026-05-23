package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.tmdb.RemoteMovie
import edu.dyds.movies.data.external.tmdb.RemoteResult

interface MovieExternalSource {
    suspend fun getMovieByTitle(title: String): RemoteMovie
}

interface MoviesExternalSource {
    suspend fun getPopularMovies(): RemoteResult
}
