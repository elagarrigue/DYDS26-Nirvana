package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.domain.entity.Movie

private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185"
private const val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780"

class MovieMapper {

    fun toDomainMovie(remoteMovie: RemoteTMDB): Movie =
        Movie(
            id = remoteMovie.id,
            title = remoteMovie.title,
            overview = remoteMovie.overview,
            releaseDate = remoteMovie.releaseDate ?: "",
            poster = buildPosterUrl(remoteMovie.posterPath),
            backdrop = buildBackdropUrl(remoteMovie.backdropPath),
            originalTitle = remoteMovie.originalTitle,
            originalLanguage = remoteMovie.originalLanguage,
            popularity = remoteMovie.popularity ?: 0.0,
            voteAverage = remoteMovie.voteAverage ?: 0.0
        )

    private fun buildPosterUrl(posterPath: String?): String =
        posterPath?.let { "$POSTER_BASE_URL$it" } ?: ""

    private fun buildBackdropUrl(backdropPath: String?): String? =
        backdropPath?.let { "$BACKDROP_BASE_URL$it" }
}
