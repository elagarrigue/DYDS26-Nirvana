package edu.dyds.movies.commonFakes

import edu.dyds.movies.data.external.MoviesRemoteDataSource
import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.tmdb.RemoteResult

class FakeMoviesRemoteDataSource : MoviesRemoteDataSource {
    var remoteResult: RemoteResult? = null
    var remoteMovie: RemoteMovie? = null
    var shouldThrow: Boolean = false
    var requestedMovieTitle: String? = null

    override suspend fun getPopularMovies(): RemoteResult {
        if (shouldThrow) throw java.io.IOException("Remote error")
        return remoteResult ?: throw Exception("No remote result set")
    }

    override suspend fun getMovieByTitle(title: String): RemoteMovie {
        requestedMovieTitle = title
        if (shouldThrow) throw java.io.IOException("Remote error")
        return remoteMovie ?: throw Exception("No remote movie set")
    }
}
