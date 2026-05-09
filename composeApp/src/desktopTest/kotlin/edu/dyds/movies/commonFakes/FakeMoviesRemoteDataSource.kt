package edu.dyds.movies.commonFakes

import edu.dyds.movies.data.remote.MoviesRemoteDataSource
import edu.dyds.movies.data.remote.RemoteMovie
import edu.dyds.movies.data.remote.RemoteResult

class FakeMoviesRemoteDataSource : MoviesRemoteDataSource {
    var remoteResult: RemoteResult? = null
    var remoteMovie: RemoteMovie? = null
    var shouldThrow: Boolean = false
    var requestedMovieId: Int? = null

    override suspend fun getPopularMovies(): RemoteResult {
        if (shouldThrow) throw java.io.IOException("Remote error")
        return remoteResult ?: throw Exception("No remote result set")
    }

    override suspend fun getMovieDetails(id: Int): RemoteMovie {
        requestedMovieId = id
        if (shouldThrow) throw java.io.IOException("Remote error")
        return remoteMovie ?: throw Exception("No remote movie set")
    }
}

