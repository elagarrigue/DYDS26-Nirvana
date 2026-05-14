package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.qualifier.MovieQualifier
import edu.dyds.movies.commonFakes.FakeMoviesRepository
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class UseCaseTests {

    private val default = Movie(
        id = 1,
        title = "Fake Movie",
        overview = "A fake overview",
        releaseDate = "2025-10-31",
        poster = "/fake_poster.jpg",
        backdrop = "/fake_backdrop.jpg",
        originalTitle = "Fake Movie Original",
        originalLanguage = "en",
        popularity = 7.5,
        voteAverage = 8.0
    )
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
        repository.movieDetail = default

        val result = getMovieDetailsUseCase.getMovieDetails(10)

        assertEquals(default, result)
    }

    @Test
    fun `al solicitar el detalle con un id específico, el repositorio recibe ese mismo id`() = runTest {
        val movieId = 42

        getMovieDetailsUseCase.getMovieDetails(movieId)

        assertEquals(movieId, repository.requestedMovieDetailId)
    }

    @Test
    fun `si el repositorio no encuentra la pelicula, getMovieDetails devuelve null`() = runTest {
        repository.movieDetail = null

        val result = getMovieDetailsUseCase.getMovieDetails(99)

        assertNull(result)
    }

    @Test
    fun `getPopularMovies devuelve peliculas calificadas y ordenadas por voteAverage`() = runTest {
        val badMovie = default.copy(id = 1, title = "Bad Movie", voteAverage = 5.9)
        val bestMovie = default.copy(id = 2, title = "Best Movie", voteAverage = 9.0)
        val goodMovie = default.copy(id = 3, title = "Good Movie", voteAverage = 6.0)
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
    fun `si el repositorio no tiene peliculas, getPopularMovies devuelve lista vacia`() = runTest {
        repository.popularMovies = emptyList()

        val result = getPopularMoviesUseCase.GetPopularMovies()

        assertEquals(emptyList<QualifiedMovie>(), result)
    }
}


