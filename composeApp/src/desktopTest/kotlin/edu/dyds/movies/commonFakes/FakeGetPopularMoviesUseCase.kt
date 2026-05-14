package edu.dyds.movies.commonFakes

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase

class FakeGetPopularMoviesUseCase : GetPopularMoviesUseCase {
    var moviesToReturn: List<QualifiedMovie> = emptyList()
    var beforeReturning: suspend () -> Unit = {}

    override suspend fun GetPopularMovies(): List<QualifiedMovie> {
        beforeReturning()
        return moviesToReturn
    }
}

