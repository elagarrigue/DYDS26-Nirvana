package edu.dyds.movies.data.external.proxy

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.PopularMoviesExternalSource
import edu.dyds.movies.data.external.tmdb.RemoteTMDB
import edu.dyds.movies.data.external.tmdb.TMDBMoviesExternalSource
import edu.dyds.movies.domain.entity.Movie

class TMDBMoviesExternalSourceProxy(
    private val tmdbMoviesExternalSource: TMDBMoviesExternalSource,
) : PopularMoviesExternalSource, MovieDetailExternalSource {
    override suspend fun getPopularMovies(): List<Movie> =
        tmdbMoviesExternalSource.getPopularMovies().results.map { it.toMovie() }

    override suspend fun getMovieByTitle(title: String): Movie? =
        tmdbMoviesExternalSource.getMovieByTitle(title)?.toMovie()
}

private const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
private const val TMDB_POSTER_SIZE = "w500"
private const val TMDB_BACKDROP_SIZE = "w780"

private fun RemoteTMDB.toMovie(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    releaseDate = releaseDate,
    poster = posterPath.toTmdbImageUrl(TMDB_POSTER_SIZE),
    backdrop = backdropPath.toTmdbImageUrl(TMDB_BACKDROP_SIZE),
    originalTitle = originalTitle,
    originalLanguage = originalLanguage,
    popularity = popularity,
    voteAverage = voteAverage,
)

private fun String?.toTmdbImageUrl(size: String): String =
    when {
        isNullOrBlank() -> ""
        startsWith("http://") || startsWith("https://") -> this
        else -> "$TMDB_IMAGE_BASE_URL/$size$this"
    }
