package edu.dyds.movies.presentation.detail

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase
import io.mockk.*
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = CoroutineScope(testDispatcher)

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
    private val useCase = mockk<GetMovieDetailsUseCase>(relaxed = true)
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DetailViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `emite carga y luego película cuando se encuentra la película`() = runTest {
        val events = mutableListOf<MovieDetailUiState>()
        val job = testScope.launch {
            viewModel.movieDetailStateFlow.collect { events.add(it) }
        }
        coEvery { useCase.getMovieDetails("Fake Movie") } returns default

        viewModel.getMovieDetail("Fake Movie")
        advanceUntilIdle()

        assertEquals(default, events.last().movie)
        coVerify(exactly = 1) { useCase.getMovieDetails("Fake Movie") }
        job.cancel()
    }

    @Test
    fun `Emite carga y luego null cuando no se encuentra la película`() = runTest {
        val events = mutableListOf<MovieDetailUiState>()
        val job = testScope.launch {
            viewModel.movieDetailStateFlow.collect { events.add(it) }
        }
        coEvery { useCase.getMovieDetails("Unknown") } returns null

        viewModel.getMovieDetail("Unknown")
        advanceUntilIdle()

        assertNull(events.last().movie)
        coVerify(exactly = 1) { useCase.getMovieDetails("Unknown") }
        job.cancel()
    }

    @Test
    fun `emite estado inicial sin pelicula`() = runTest {
        val initialState = viewModel.movieDetailStateFlow.first()

        assertNull(initialState.movie)
    }

    @Test
    fun `cuando se solicita otra pelicula, actualiza el detalle`() = runTest {
        val events = mutableListOf<MovieDetailUiState>()
        val job = testScope.launch {
            viewModel.movieDetailStateFlow.collect { events.add(it) }
        }
        val otherMovie = default.copy(id = 2, title = "Fake Movie 2")
        coEvery { useCase.getMovieDetails("Fake Movie") } returns default
        coEvery { useCase.getMovieDetails("Fake Movie 2") } returns otherMovie

        viewModel.getMovieDetail("Fake Movie")
        advanceUntilIdle()

        viewModel.getMovieDetail("Fake Movie 2")
        advanceUntilIdle()

        assertTrue(events.any { it.movie == default })
        assertEquals(otherMovie, events.last().movie)
        coVerify(exactly = 1) { useCase.getMovieDetails("Fake Movie") }
        coVerify(exactly = 1) { useCase.getMovieDetails("Fake Movie 2") }
        job.cancel()
    }

    @Test
    fun `isLoading es true mientras se obtiene la pelicula`() = runTest {
        val events = mutableListOf<MovieDetailUiState>()
        val job = testScope.launch {
            viewModel.movieDetailStateFlow.collect { events.add(it) }
        }
        coEvery { useCase.getMovieDetails("Fake Movie") } coAnswers {
            delay(10)
            default
        }

        viewModel.getMovieDetail("Fake Movie")

        assertTrue(events.any { it.isLoading })

        advanceUntilIdle()

        val final = events.last()
        assertFalse(final.isLoading)
        assertEquals(default, final.movie)
        job.cancel()
    }
}
