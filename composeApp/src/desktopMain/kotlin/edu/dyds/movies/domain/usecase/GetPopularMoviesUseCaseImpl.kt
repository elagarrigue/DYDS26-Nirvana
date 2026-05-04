package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.domain.qualifier.MovieQualifier

class GetPopularMoviesUseCaseImpl(
    private val repository: MoviesRepository,
    private val movieQualifier: MovieQualifier
) : GetPopularMoviesUseCase {
    override suspend fun GetPopularMovies(): List<QualifiedMovie> =
        movieQualifier.qualifyMovies(repository.getPopularMovies())
}

