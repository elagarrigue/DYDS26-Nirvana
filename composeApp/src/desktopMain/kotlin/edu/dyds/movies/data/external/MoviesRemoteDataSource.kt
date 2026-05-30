package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.tmdb.RemoteTMDB
import edu.dyds.movies.data.external.tmdb.RemoteResult

interface MovieExternalSource {
    suspend fun getMovieByTitle(title: String): RemoteTMDB?
}

interface MoviesExternalSource {
    suspend fun getPopularMovies(): RemoteResult
}
