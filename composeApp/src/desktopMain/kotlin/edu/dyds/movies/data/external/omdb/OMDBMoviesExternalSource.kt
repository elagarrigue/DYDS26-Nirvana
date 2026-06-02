package edu.dyds.movies.data.external.omdb

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import edu.dyds.movies.data.external.omdb.RemoteOMDB as OmdbRemoteMovie

class OMDBMoviesExternalSource(
    private val omdbHttpClient: HttpClient,
) {
    suspend fun getMovieByTitle(title: String): OmdbRemoteMovie =
        getOMDBMovieDetails(title)

    private suspend fun getOMDBMovieDetails(title: String): OmdbRemoteMovie =
        omdbHttpClient.get("/?t=") {
            parameter("t", title)
        }.body<OmdbRemoteMovie>()
}
