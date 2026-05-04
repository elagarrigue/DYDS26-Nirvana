package edu.dyds.movies.data.remote.mapper

import edu.dyds.movies.data.remote.RemoteMovie
import edu.dyds.movies.domain.entity.Movie

private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185"
private const val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780"

class MovieMapper {

    fun toDomainMovie(remoteMovie: RemoteMovie): Movie =
        Movie(
            id = remoteMovie.id,
            title = remoteMovie.title,
            overview = remoteMovie.overview,
            releaseDate = remoteMovie.releaseDate,
            poster = buildPosterUrl(remoteMovie.posterPath),
            backdrop = buildBackdropUrl(remoteMovie.backdropPath),
            originalTitle = remoteMovie.originalTitle,
            originalLanguage = remoteMovie.originalLanguage,
            popularity = remoteMovie.popularity,
            voteAverage = remoteMovie.voteAverage
        )

    private fun buildPosterUrl(posterPath: String): String =
        "$POSTER_BASE_URL$posterPath"

    private fun buildBackdropUrl(backdropPath: String?): String? =
        backdropPath?.let { "$BACKDROP_BASE_URL$it" }
}
