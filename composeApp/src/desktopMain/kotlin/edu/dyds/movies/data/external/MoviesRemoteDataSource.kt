package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.tmdb.RemoteResult
import edu.dyds.movies.domain.entity.Movie

interface MovieExternalSource {
    suspend fun getMovieByTitle(title: String): Movie?
}

interface MoviesExternalSource {
    suspend fun getPopularMovies(): RemoteResult
}
