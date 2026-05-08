package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.qualifier.MovieQualifier
import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.commonFakes.fakeMovie
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class UseCaseTests {

    private lateinit var repository: FakeMoviesRepository
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase
    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase

    @Before
    fun setUp() {
        repository = FakeMoviesRepository()
        getMovieDetailsUseCase = GetMovieDetailsUseCaseImpl(repository)
        getPopularMoviesUseCase = GetPopularMoviesUseCaseImpl(repository, MovieQualifier())
    }

    @Test
    fun `getMovieDetails devuelve la pelicula del repositorio`() = runTest {
        val movie = fakeMovie(id = 10, title = "The Matrix")
        repository.movieDetail = movie

        val result = getMovieDetailsUseCase.getMovieDetails(10)

        assertEquals(movie, result)
    }

    @Test
    fun `getMovieDetails busca la pelicula por id`() = runTest {
        val movieId = 42

        getMovieDetailsUseCase.getMovieDetails(movieId)

        assertEquals(movieId, repository.requestedMovieDetailId)
    }

    @Test
    fun `getMovieDetails devuelve null si el repositorio no encuentra la pelicula`() = runTest {
        repository.movieDetail = null

        val result = getMovieDetailsUseCase.getMovieDetails(99)

        assertNull(result)
    }

    @Test
    fun `GetPopularMovies devuelve peliculas calificadas y ordenadas por voteAverage`() = runTest {
        val badMovie = fakeMovie(id = 1, title = "Bad Movie", voteAverage = 5.9)
        val bestMovie = fakeMovie(id = 2, title = "Best Movie", voteAverage = 9.0)
        val goodMovie = fakeMovie(id = 3, title = "Good Movie", voteAverage = 6.0)
        repository.popularMovies = listOf(badMovie, bestMovie, goodMovie)

        val result = getPopularMoviesUseCase.GetPopularMovies()

        assertEquals(
            listOf(
                QualifiedMovie(bestMovie, isGoodMovie = true),
                QualifiedMovie(goodMovie, isGoodMovie = true),
                QualifiedMovie(badMovie, isGoodMovie = false)
            ),
            result
        )
    }

    @Test
    fun `GetPopularMovies devuelve lista vacia si el repositorio no tiene peliculas`() = runTest {
        repository.popularMovies = emptyList()

        val result = getPopularMoviesUseCase.GetPopularMovies()

        assertEquals(emptyList<QualifiedMovie>(), result)
    }
}

private class FakeMoviesRepository : MoviesRepository {
    var popularMovies: List<Movie> = emptyList()
    var movieDetail: Movie? = null
    var requestedMovieDetailId: Int? = null

    override suspend fun getPopularMovies(): List<Movie> = popularMovies

    override suspend fun getMovieDetail(id: Int): Movie? {
        requestedMovieDetailId = id
        return movieDetail
    }
}

