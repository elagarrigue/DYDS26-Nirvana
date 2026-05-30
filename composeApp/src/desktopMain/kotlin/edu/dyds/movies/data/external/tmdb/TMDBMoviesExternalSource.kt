package edu.dyds.movies.data.external.tmdb

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TMDBMoviesExternalSource(
    private val httpClient: HttpClient
) {
    suspend fun getPopularMovies(): RemoteResult =
        httpClient.get("/3/discover/movie?sort_by=popularity.desc").body()

    suspend fun getMovieByTitle(title: String): RemoteTMDB? =
        httpClient.get("/3/search/movie") {
            parameter("query", title)
        }.body<RemoteResult>().results.firstOrNull()
}
