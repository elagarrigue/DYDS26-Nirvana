package edu.dyds.movies.domain.qualifier

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie

private const val MIN_VOTE_AVERAGE = 6.0

class MovieQualifier {
    fun qualifyMovies(movies: List<Movie>): List<QualifiedMovie> =
        movies
            .sortedByDescending { it.voteAverage }
            .map { qualifyMovie(it) }

    private fun qualifyMovie(movie: Movie): QualifiedMovie =
        QualifiedMovie(
            movie = movie,
            isGoodMovie = qualifies(movie.voteAverage)
        )

    private fun qualifies(voteAverage: Double): Boolean =
        voteAverage >= MIN_VOTE_AVERAGE
}
