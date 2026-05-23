package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.MoviesExternalSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TMDBMoviesExternalSource(
    private val httpClient: HttpClient
) : MovieExternalSource, MoviesExternalSource {
    override suspend fun getPopularMovies(): RemoteResult =
        httpClient.get("/3/discover/movie?sort_by=popularity.desc").body()

    override suspend fun getMovieByTitle(title: String): RemoteMovie =
        httpClient.get("/3/search/movie") {
            parameter("query", title)
        }.body<RemoteResult>().results.first()
}
