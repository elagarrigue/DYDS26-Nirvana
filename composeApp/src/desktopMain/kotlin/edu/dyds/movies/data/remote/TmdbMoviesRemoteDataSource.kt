package edu.dyds.movies.data.remote

import edu.dyds.movies.domain.entity.RemoteMovie
import edu.dyds.movies.domain.entity.RemoteResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TmdbMoviesRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getPopularMovies(): RemoteResult =
        httpClient.get("/3/discover/movie?sort_by=popularity.desc").body()

    suspend fun getMovieDetails(id: Int): RemoteMovie =
        httpClient.get("/3/movie/$id").body()
}
