package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie

class MovieExternalSourceBroker(
    private val tmdbMoviesExternalSource: MovieDetailExternalSource,
    private val omdbMoviesExternalSource: MovieDetailExternalSource
) : MovieDetailExternalSource {

    override suspend fun getMovieByTitle(title: String): Movie? {
        val tmdbMovie = runCatching { tmdbMoviesExternalSource.getMovieByTitle(title) }.getOrNull()
        val omdbMovie = runCatching { omdbMoviesExternalSource.getMovieByTitle(title) }.getOrNull()

        return when {
            tmdbMovie != null && omdbMovie != null -> buildMovie(tmdbMovie, omdbMovie)
            tmdbMovie != null -> buildOnlyTMDBMovie(tmdbMovie)
            omdbMovie != null -> buildOnlyOMDBMovie(omdbMovie)
            else -> null
        }
    }

    private fun buildMovie(tmdbMovie: Movie, omdbMovie: Movie): Movie {
        return tmdbMovie.copy(
            overview = "TMDB: ${tmdbMovie.overview}\n\nOMDB: ${omdbMovie.overview}",
            popularity = (omdbMovie.popularity + tmdbMovie.popularity) / 2.0,
            voteAverage = (omdbMovie.voteAverage + tmdbMovie.voteAverage) / 2.0
        )
    }

    private fun buildOnlyOMDBMovie(movie: Movie): Movie {
        return movie.copy(overview = "OMDB: ${movie.overview}")
    }

    private fun buildOnlyTMDBMovie(movie: Movie): Movie {
        return movie.copy(overview = "TMDB: ${movie.overview}")
    }
}
