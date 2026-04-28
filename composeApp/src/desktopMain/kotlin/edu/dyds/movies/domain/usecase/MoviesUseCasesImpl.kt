package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.domain.qualifier.MovieQualifier

class MoviesUseCasesImpl(
    private val repository: MoviesRepository,
    private val movieQualifier: MovieQualifier
) : MoviesUseCases {
    override suspend fun getMovieDetails(id: Int): Movie? = repository.getMovieDetail(id)
    override suspend fun getPopularMovies(): List<QualifiedMovie> =
        movieQualifier.qualifyMovies(repository.getPopularMovies())
}
