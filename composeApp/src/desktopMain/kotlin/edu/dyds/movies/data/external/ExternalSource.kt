package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.tmdb.RemoteResult
import edu.dyds.movies.data.external.tmdb.RemoteTMDB

interface ExternalSource {
    suspend fun getPopularMovies(): RemoteResult
    suspend fun getMovieByTitle(title: String): RemoteTMDB
}