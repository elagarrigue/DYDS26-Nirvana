package edu.dyds.movies.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class MoviesRemoteDataSourceImpl(
    private val httpClient: HttpClient
) : MoviesRemoteDataSource {
    override suspend fun getPopularMovies(): RemoteResult =
        httpClient.get("/3/discover/movie?sort_by=popularity.desc").body()

    override suspend fun getMovieDetails(id: Int): RemoteMovie =
        httpClient.get("/3/movie/$id").body()
}
