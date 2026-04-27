package edu.dyds.movies.data.remote

import edu.dyds.movies.domain.entity.RemoteMovie
import edu.dyds.movies.domain.entity.RemoteResult

interface MoviesRemoteDataSource {
    suspend fun getPopularMovies(): RemoteResult

    suspend fun getMovieDetails(id: Int): RemoteMovie
}