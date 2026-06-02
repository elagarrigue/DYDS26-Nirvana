package edu.dyds.movies.data.repository

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.PopularMoviesExternalSource
import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.domain.entity.Movie
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MoviesRepositoryImplTest {

    private lateinit var movieDetailExternalSource: MovieDetailExternalSource
    private lateinit var popularMoviesExternalSource: PopularMoviesExternalSource
    private lateinit var localDataSource: MoviesLocalDataSource
    private lateinit var repository: MoviesRepositoryImpl

    @BeforeTest
    fun setup() {
        movieDetailExternalSource = mockk()
        popularMoviesExternalSource = mockk()
        localDataSource = mockk()
        repository = MoviesRepositoryImpl(
            movieDetailExternalSource = movieDetailExternalSource,
            popularMoviesExternalSource = popularMoviesExternalSource,
            localDataSource = localDataSource
        )
    }

    @Test
    fun `si hay cache de populares devuelve cache y no consulta remoto`() = runTest {
        val cachedMovies = listOf(movie(id = 1, title = "Cached"))
        coEvery { localDataSource.getPopularMoviesFromCache() } returns cachedMovies

        val result = repository.getPopularMovies()

        assertEquals(cachedMovies, result)
        coVerify(exactly = 0) { popularMoviesExternalSource.getPopularMovies() }
        coVerify(exactly = 0) { localDataSource.savePopularMovies(any()) }
    }

    @Test
    fun `si cache popular esta vacia consulta remoto y guarda en cache`() = runTest {
        val remoteMovies = listOf(movie(id = 2, title = "Remote"))
        coEvery { localDataSource.getPopularMoviesFromCache() } returns emptyList()
        coEvery { popularMoviesExternalSource.getPopularMovies() } returns remoteMovies
        coEvery { localDataSource.savePopularMovies(remoteMovies) } returns Unit

        val result = repository.getPopularMovies()

        assertEquals(remoteMovies, result)
        coVerify(exactly = 1) { popularMoviesExternalSource.getPopularMovies() }
        coVerify(exactly = 1) { localDataSource.savePopularMovies(remoteMovies) }
    }

    @Test
    fun `si detalle remoto falla usa cache local como fallback`() = runTest {
        val cachedMovie = movie(id = 3, title = "Inception", overview = "Cached overview")
        coEvery { movieDetailExternalSource.getMovieByTitle("Inception") } throws java.io.IOException("boom")
        coEvery { localDataSource.getMovieDetailFromCache("Inception") } returns cachedMovie

        val result = repository.getMovieByTitle("Inception")

        assertEquals(cachedMovie, result)
        coVerify(exactly = 1) { movieDetailExternalSource.getMovieByTitle("Inception") }
        coVerify(exactly = 1) { localDataSource.getMovieDetailFromCache("Inception") }
    }

    @Test
    fun `si detalle remoto devuelve una pelicula la retorna sin consultar cache`() = runTest {
        val remoteMovie = movie(id = 4, title = "Dune", overview = "Remote overview")
        coEvery { movieDetailExternalSource.getMovieByTitle("Dune") } returns remoteMovie

        val result = repository.getMovieByTitle("Dune")

        assertEquals(remoteMovie, result)
        coVerify(exactly = 1) { movieDetailExternalSource.getMovieByTitle("Dune") }
        coVerify(exactly = 0) { localDataSource.getMovieDetailFromCache(any()) }
    }

    @Test
    fun `si detalle remoto falla y cache esta vacia devuelve null`() = runTest {
        coEvery { movieDetailExternalSource.getMovieByTitle("Unknown") } throws java.io.IOException("boom")
        coEvery { localDataSource.getMovieDetailFromCache("Unknown") } returns null

        val result = repository.getMovieByTitle("Unknown")

        assertNull(result)
        coVerify(exactly = 1) { movieDetailExternalSource.getMovieByTitle("Unknown") }
        coVerify(exactly = 1) { localDataSource.getMovieDetailFromCache("Unknown") }
    }

    private fun movie(
        id: Int,
        title: String,
        overview: String = "Overview",
        releaseDate: String = "2024-01-01",
        poster: String = "https://poster.jpg",
        backdrop: String? = "https://backdrop.jpg",
        originalTitle: String = title,
        originalLanguage: String = "en",
        popularity: Double = 8.0,
        voteAverage: Double = 7.5
    ): Movie = Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        poster = poster,
        backdrop = backdrop,
        originalTitle = originalTitle,
        originalLanguage = originalLanguage,
        popularity = popularity,
        voteAverage = voteAverage
    )
}
