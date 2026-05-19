package edu.dyds.movies.commonFakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class FakeMoviesRepository : MoviesRepository {
    var popularMovies: List<Movie> = emptyList()
    var movieDetail: Movie? = null
    var requestedMovieDetailTitle: String? = null

    override suspend fun getPopularMovies(): List<Movie> = popularMovies

    override suspend fun getMovieDetail(title: String): Movie? {
        requestedMovieDetailTitle = title
        return movieDetail
    }
}

