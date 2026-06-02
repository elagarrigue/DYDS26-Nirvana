package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.qualifier.MovieQualifier
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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

    lateinit var repository: MoviesRepository

    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase
    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getMovieDetailsUseCase = GetMovieDetailsUseCaseImpl(repository)
        getPopularMoviesUseCase = GetPopularMoviesUseCaseImpl(repository, MovieQualifier())
    }

    @Test
    fun `dado un repositorio con la pelicula, al solicitar el detalle, devuelve esa pelicula`() = runTest {
        coEvery { repository.getMovieByTitle("Fake Movie") } returns default

        val result = getMovieDetailsUseCase.getMovieDetails("Fake Movie")

        assertEquals(default, result)
    }

    @Test
    fun `al solicitar el detalle con un titulo específico, el repositorio recibe ese mismo titulo`() = runTest {
        val movieTitle = "The Matrix"
        coEvery { repository.getMovieByTitle(movieTitle) } returns null

        getMovieDetailsUseCase.getMovieDetails(movieTitle)

        coVerify(exactly = 1) { repository.getMovieByTitle(movieTitle) }
    }

    @Test
    fun `si el repositorio no encuentra la pelicula, getMovieDetails devuelve null`() = runTest {
        coEvery { repository.getMovieByTitle("Unknown") } returns null

        val result = getMovieDetailsUseCase.getMovieDetails("Unknown")

        assertNull(result)
    }

    @Test
    fun `dado un repositorio con peliculas, getPopularMovies devuelve peliculas calificadas y ordenadas por voteAverage`() = runTest {
        val badMovie = default.copy(id = 1, title = "Bad Movie", voteAverage = 5.9)
        val bestMovie = default.copy(id = 2, title = "Best Movie", voteAverage = 9.0)
        val goodMovie = default.copy(id = 3, title = "Good Movie", voteAverage = 6.0)

        coEvery { repository.getPopularMovies() } returns listOf(badMovie, bestMovie, goodMovie)

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
        coEvery { repository.getPopularMovies() } returns emptyList()

        val result = getPopularMoviesUseCase.GetPopularMovies()

        assertEquals(emptyList<QualifiedMovie>(), result)
    }
}