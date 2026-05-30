package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TMDBMoviesExternalSource(
    private val httpClient: HttpClient,
    private val movieMapper: MovieMapper = MovieMapper()
) : MovieExternalSource, MoviesExternalSource {
    override suspend fun getPopularMovies(): List<Movie> =
        httpClient.get("/3/discover/movie?sort_by=popularity.desc")
            .body<RemoteResult>()
            .results
            .map(movieMapper::toDomainMovie)

    override suspend fun getMovieByTitle(title: String): Movie? =
        httpClient.get("/3/search/movie") {
            parameter("query", title)
        }.body<RemoteResult>().results.firstOrNull()?.let { movieMapper.toDomainMovie(it) }
}
