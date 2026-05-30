package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class OMDBMoviesExternalSource(
    private val omdbHttpClient: HttpClient,
    private val movieMapper: OmdbMovieMapper = OmdbMovieMapper(),
) : MovieExternalSource {
    override suspend fun getMovieByTitle(title: String): Movie? {
        return runCatching { movieMapper.toDomainMovie(getOMDBMovieDetails(title)) }.getOrNull()
    }

    private suspend fun getOMDBMovieDetails(title: String): RemoteOMDB =
        omdbHttpClient.get("/?t=") {
            parameter("t", title)
        }.body<RemoteOMDB>()
}