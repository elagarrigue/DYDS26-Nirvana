package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.tmdb.RemoteTMDB

class MovieExternalSourceBroker(
    private val tmdbMoviesExternalSource: MovieExternalSource,
    private val omdbMoviesExternalSource: MovieExternalSource
) : MovieExternalSource {

    override suspend fun getMovieByTitle(title: String): RemoteTMDB? {
        val tmdbMovie = runCatching { tmdbMoviesExternalSource.getMovieByTitle(title) }.getOrNull()
        val omdbMovie = runCatching { omdbMoviesExternalSource.getMovieByTitle(title) }.getOrNull()

        return when {
            tmdbMovie != null && omdbMovie != null -> buildMovie(tmdbMovie, omdbMovie)
            tmdbMovie != null -> withOverviewPrefix("TMDB", tmdbMovie)
            omdbMovie != null -> withOverviewPrefix("OMDB", omdbMovie)
            else -> null
        }
    }

    private fun withOverviewPrefix(prefix: String, movie: RemoteTMDB): RemoteTMDB =
        movie.copy(overview = "$prefix: ${movie.overview}")

    private fun buildMovie(tmdbMovie: RemoteTMDB, omdbMovie: RemoteTMDB): RemoteTMDB =
        tmdbMovie.copy(
            overview = "TMDB: ${tmdbMovie.overview}\n\nOMDB: ${omdbMovie.overview}",
            popularity = (omdbMovie.popularity + tmdbMovie.popularity) / 2.0,
            voteAverage = (omdbMovie.voteAverage + tmdbMovie.voteAverage) / 2.0
        )

}
