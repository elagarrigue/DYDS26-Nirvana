package edu.dyds.movies.commonFakes

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.PopularMoviesExternalSource
import edu.dyds.movies.domain.entity.Movie

class FakeMoviesExternalSource : PopularMoviesExternalSource {
    var remoteMovies: List<Movie>? = null
    var shouldThrow: Boolean = false

    override suspend fun getPopularMovies(): List<Movie> {
        if (shouldThrow) throw java.io.IOException("Remote error")
        return remoteMovies ?: throw Exception("No remote movies set")
    }
}

class FakeMovieExternalSource : MovieDetailExternalSource {
    var remoteMovie: Movie? = null
    var shouldThrow: Boolean = false
    var requestedMovieTitle: String? = null

    override suspend fun getMovieByTitle(title: String): Movie? {
        requestedMovieTitle = title
        if (shouldThrow) throw java.io.IOException("Remote error")
        return remoteMovie
    }
}
