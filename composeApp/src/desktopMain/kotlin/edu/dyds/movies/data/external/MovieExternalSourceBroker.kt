package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.proxy.OMDBMoviesExternalSourceProxy
import edu.dyds.movies.data.external.proxy.TMDBMoviesExternalSourceProxy
import edu.dyds.movies.data.external.tmdb.RemoteTMDB
import edu.dyds.movies.data.external.tmdb.RemoteResult
import java.io.IOException

class MovieExternalSourceBroker(
    private val tmdbMoviesExternalSourceProxy: TMDBMoviesExternalSourceProxy,
    private val omdbMoviesExternalSourceProxy: OMDBMoviesExternalSourceProxy
) : ExternalSource {

    override suspend fun getPopularMovies(): RemoteResult {
       return tmdbMoviesExternalSourceProxy.getPopularMovies()
    }

    override suspend fun getMovieByTitle(title: String): RemoteTMDB {
        val tmdbMovie = runCatching { tmdbMoviesExternalSourceProxy.getMovieByTitle(title) }.getOrNull()
        val omdbMovie = runCatching { omdbMoviesExternalSourceProxy.getMovieByTitle(title) }.getOrNull()

        return when {
            tmdbMovie != null && omdbMovie != null -> buildMovie(tmdbMovie, omdbMovie)
            tmdbMovie != null -> tmdbMovie
            omdbMovie != null -> omdbMovie
            else -> throw IOException("Movie not found in TMDB nor OMDB: $title")
        }
    }

    private fun buildMovie(tmdbMovie: RemoteTMDB, omdbMovie: RemoteTMDB): RemoteTMDB =
        tmdbMovie.copy(
            overview = "TMDB: ${tmdbMovie.overview}\n\nOMDB: ${omdbMovie.overview}",
            popularity = (omdbMovie.popularity + tmdbMovie.popularity) / 2.0,
            voteAverage = (omdbMovie.voteAverage + tmdbMovie.voteAverage) / 2.0
        )

}
