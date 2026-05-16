package edu.dyds.movies.commonFakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase

class FakeGetMovieDetailsUseCase : GetMovieDetailsUseCase {
    var movieToReturn: Movie? = null

    override suspend fun getMovieDetails(id: Int): Movie? {
        return movieToReturn
    }
}
