package edu.dyds.movies.data.qualifier

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.entity.RemoteMovie

private const val MIN_VOTE_AVERAGE = 6.0

object MovieQualifier {

    fun buildQualifiedMovies(
        remoteMovies: List<RemoteMovie>,
        domainMovies: List<Movie>
    ): List<QualifiedMovie> {
        val moviesById = domainMovies.associateBy { it.id }

        return remoteMovies
            .sortedByDescending { it.voteAverage }
            .mapNotNull { remoteMovie -> toQualifiedMovie(remoteMovie, moviesById) }
    }

    private fun toQualifiedMovie(
        remoteMovie: RemoteMovie,
        moviesById: Map<Int, Movie>
    ): QualifiedMovie? =
        moviesById[remoteMovie.id]?.let { domainMovie ->
            QualifiedMovie(
                movie = domainMovie,
                isGoodMovie = qualifies(remoteMovie.voteAverage)
            )
        }

    fun qualifyMovie(movie: Movie): QualifiedMovie =
        QualifiedMovie(
            movie = movie,
            isGoodMovie = qualifies(movie.voteAverage)
        )

    private fun qualifies(voteAverage: Double): Boolean =
        voteAverage >= MIN_VOTE_AVERAGE
}
