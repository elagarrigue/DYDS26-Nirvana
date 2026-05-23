package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.tmdb.RemoteMovie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import edu.dyds.movies.data.external.omdb.RemoteMovie as OmdbRemoteMovie

internal class OMDBMoviesExternalSource(
    private val omdbHttpClient: HttpClient,
) : MovieExternalSource {
    override suspend fun getMovieByTitle(title: String): RemoteMovie {
        return getOMDBMovieDetails(title).toDomainMovie()
    }

    private suspend fun getOMDBMovieDetails(title: String): OmdbRemoteMovie =
        omdbHttpClient.get("/?t=") {
            parameter("t", title)
        }.body<OmdbRemoteMovie>()
}