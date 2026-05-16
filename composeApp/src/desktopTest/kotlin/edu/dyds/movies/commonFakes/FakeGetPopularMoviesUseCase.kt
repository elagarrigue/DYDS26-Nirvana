package edu.dyds.movies.commonFakes

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase

class FakeGetPopularMoviesUseCase : GetPopularMoviesUseCase {
    var moviesToReturn: List<QualifiedMovie> = emptyList()

    override suspend fun GetPopularMovies(): List<QualifiedMovie> {
        return moviesToReturn
    }
}

