package edu.dyds.movies.commonFakes

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.domain.entity.Movie

class FakeMoviesExternalSource : MoviesExternalSource {
    var remoteMovies: List<Movie>? = null
    var shouldThrow: Boolean = false

    override suspend fun getPopularMovies(): List<Movie> {
        if (shouldThrow) throw java.io.IOException("Remote error")
        return remoteMovies ?: throw Exception("No remote movies set")
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
