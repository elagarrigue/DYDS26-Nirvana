package edu.dyds.movies.data.external.proxy

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.omdb.OMDBMoviesExternalSource
import edu.dyds.movies.data.external.omdb.RemoteOMDB
import edu.dyds.movies.domain.entity.Movie

class OMDBMoviesExternalSourceProxy(
    private val omdbMoviesExternalSource: OMDBMoviesExternalSource,
) : MovieDetailExternalSource {
    override suspend fun getMovieByTitle(title: String): Movie? =
        omdbMoviesExternalSource.getMovieByTitle(title).toMovie()
}

private fun RemoteOMDB.toMovie(): Movie {
    val imdb = imdbRating
        .takeIf { it.isNotEmpty() && it != "N/A" }
        ?.toDoubleOrNull() ?: 0.0
    val vote = metaScore
        .takeIf { it.isNotEmpty() && it != "N/A" }
        ?.toDoubleOrNull() ?: 0.0

    return Movie(
        id = title.hashCode(),
        title = title,
        overview = plot,
        releaseDate = if (released.isNotEmpty() && released != "N/A") released else year,
        poster = poster,
        backdrop = poster,
        originalTitle = title,
        originalLanguage = language,
        popularity = imdb,
        voteAverage = vote,
    )
}
