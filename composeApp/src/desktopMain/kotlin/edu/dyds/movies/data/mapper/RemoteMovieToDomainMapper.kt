package edu.dyds.movies.data.mapper

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.RemoteMovie

/**
 * Mapper para convertir RemoteMovie (modelo de datos remota) a Movie (modelo de dominio)
 */
class RemoteMovieToDomainMapper : Mapper<RemoteMovie, Movie> {
    override fun map(input: RemoteMovie): Movie {
        return Movie(
            id = input.id,
            title = input.title,
            overview = input.overview,
            releaseDate = input.releaseDate,
            poster = "https://image.tmdb.org/t/p/w185${input.posterPath}",
            backdrop = input.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
            originalTitle = input.originalTitle,
            originalLanguage = input.originalLanguage,
            popularity = input.popularity,
            voteAverage = input.voteAverage
        )
    }
}

