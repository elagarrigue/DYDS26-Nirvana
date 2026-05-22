package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.tmdb.RemoteResult

interface MoviesRemoteDataSource {
    suspend fun getPopularMovies(): RemoteResult

    suspend fun getMovieByTitle(title: String): RemoteMovie
}