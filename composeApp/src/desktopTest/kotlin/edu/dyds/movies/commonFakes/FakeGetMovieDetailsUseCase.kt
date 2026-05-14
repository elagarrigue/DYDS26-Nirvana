package edu.dyds.movies.commonFakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase

class FakeGetMovieDetailsUseCase : GetMovieDetailsUseCase {
    var movieToReturn: Movie? = null
    var beforeReturning: suspend () -> Unit = {}

    override suspend fun getMovieDetails(id: Int): Movie? {
        beforeReturning()
        return movieToReturn
    }
}

