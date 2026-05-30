package edu.dyds.movies.commonFakes

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.data.external.tmdb.RemoteResult
import edu.dyds.movies.domain.entity.Movie

class FakeMoviesExternalSource : MoviesExternalSource {
    var remoteResult: RemoteResult? = null
    var shouldThrow: Boolean = false

    override suspend fun getPopularMovies(): RemoteResult {
        if (shouldThrow) throw java.io.IOException("Remote error")
        return remoteResult ?: throw Exception("No remote result set")
    }
}

class FakeMovieExternalSource : MovieExternalSource {
    var remoteMovie: Movie? = null
    var shouldThrow: Boolean = false
    var requestedMovieTitle: String? = null

    override suspend fun getMovieByTitle(title: String): Movie? {
        requestedMovieTitle = title
        if (shouldThrow) throw java.io.IOException("Remote error")
        return remoteMovie
    }
}
