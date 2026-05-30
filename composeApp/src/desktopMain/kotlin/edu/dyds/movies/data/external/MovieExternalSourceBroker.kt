package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie

class MovieExternalSourceBroker(
    private val tmdbMoviesExternalSource: MovieExternalSource,
    private val omdbMoviesExternalSource: MovieExternalSource
) : MovieExternalSource {

    override suspend fun getMovieByTitle(title: String): Movie? {
        val tmdbMovie = runCatching { tmdbMoviesExternalSource.getMovieByTitle(title) }.getOrNull()
        val omdbMovie = runCatching { omdbMoviesExternalSource.getMovieByTitle(title) }.getOrNull()

        return when {
            tmdbMovie != null && omdbMovie != null -> buildMovie(tmdbMovie, omdbMovie)
            tmdbMovie != null -> withOverviewPrefix("TMDB", tmdbMovie)
            omdbMovie != null -> withOverviewPrefix("OMDB", omdbMovie)
            else -> null
        }
    }

    private fun withOverviewPrefix(prefix: String, movie: Movie): Movie =
        movie.copy(overview = "$prefix: ${movie.overview}")

    private fun buildMovie(tmdbMovie: Movie, omdbMovie: Movie): Movie =
        tmdbMovie.copy(
            overview = "TMDB: ${tmdbMovie.overview}\n\nOMDB: ${omdbMovie.overview}",
            popularity = (omdbMovie.popularity + tmdbMovie.popularity) / 2.0,
            voteAverage = (omdbMovie.voteAverage + tmdbMovie.voteAverage) / 2.0
        )

}
