package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import edu.dyds.movies.data.external.omdb.RemoteOMDB as OmdbRemoteMovie

class OMDBMoviesExternalSource(
    private val omdbHttpClient: HttpClient,
) : MovieExternalSource {
    override suspend fun getMovieByTitle(title: String): Movie? {
        return runCatching { getOMDBMovieDetails(title).toDomainMovie() }.getOrNull()
    }

    private suspend fun getOMDBMovieDetails(title: String): OmdbRemoteMovie =
        omdbHttpClient.get("/?t=") {
            parameter("t", title)
        }.body<OmdbRemoteMovie>()
}