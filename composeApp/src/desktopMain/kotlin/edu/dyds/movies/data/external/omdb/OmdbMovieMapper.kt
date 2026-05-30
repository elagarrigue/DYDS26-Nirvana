package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.domain.entity.Movie

class OmdbMovieMapper {
    fun toDomainMovie(remoteMovie: RemoteOMDB): Movie {
        val imdb = remoteMovie.imdbRating
            .takeIf { it.isNotEmpty() && it != "N/A" }
            ?.toDoubleOrNull() ?: 0.0
        val vote = if (remoteMovie.metaScore.isNotEmpty() && remoteMovie.metaScore != "N/A") {
            remoteMovie.metaScore.toDouble()
        } else {
            0.0
        }

        return Movie(
            id = remoteMovie.title.hashCode(),
            title = remoteMovie.title,
            overview = remoteMovie.plot,
            releaseDate = if (remoteMovie.released.isNotEmpty() && remoteMovie.released != "N/A") {
                remoteMovie.released
            } else {
                remoteMovie.year
            },
            poster = remoteMovie.poster,
            backdrop = remoteMovie.poster,
            originalTitle = remoteMovie.title,
            originalLanguage = remoteMovie.language,
            popularity = imdb,
            voteAverage = vote
        )
    }
}

