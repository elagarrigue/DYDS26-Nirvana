package edu.dyds.movies.presentation.home

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)

class HomeViewModelTest {

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

    private val defaultGoodMovie = QualifiedMovie(
        movie = default,
        isGoodMovie = true
    )
    private val defaultBadMovie = QualifiedMovie(
        movie = default,
        isGoodMovie = false
    )
    private val useCase = mockk<GetPopularMoviesUseCase>(relaxed = true)
    private lateinit var viewModel: HomeViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = CoroutineScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `El primer estado del ViewModel es isLoading false y la lista vacia`() = runTest {
        val events = ArrayList<MoviesUiState>()
        val movies = emptyList<QualifiedMovie>()

        val job = testScope.launch { viewModel.moviesStateFlow.collect { events.add(it) } }

        assertEquals(false, events.first().isLoading)
        assertEquals(movies, events.first().movies)
        job.cancel()
    }


    @Test
    fun `Al llamar getAllMovies con una lista no nula, emite isLoading false y las peliculas al finalizar`() = runTest {
        val events = ArrayList<MoviesUiState>()
        val movies = listOf<QualifiedMovie>(defaultGoodMovie, defaultBadMovie)
        val job = testScope.launch {
            viewModel.moviesStateFlow.collect { events.add(it) }
        }

        coEvery { useCase.GetPopularMovies() } returns movies

        viewModel.getAllMovies()

        advanceUntilIdle()

        assertEquals(false, events.last().isLoading)
        assertEquals(movies, events.last().movies)
        coVerify(exactly = 1) { useCase.GetPopularMovies() }
        job.cancel()
    }


    @Test
    fun `Al llamar getAllMovies con una lista vacia, emite isLoading false y la lista vacia al finalizar`() = runTest {
        val events = ArrayList<MoviesUiState>()
        val job =testScope.launch { viewModel.moviesStateFlow.collect { events.add(it) } }

        coEvery { useCase.GetPopularMovies() } returns emptyList()

        viewModel.getAllMovies()
        advanceUntilIdle()

        assertFalse(events.last().isLoading)
        assertEquals(emptyList<QualifiedMovie>(), events.last().movies)
        job.cancel()
    }

    @Test
    fun `Al llamar getAllMovies, emite isLoading es true y luego el emite isLoading false y la lista de peliculas`() = runTest {
        val emissions = arrayListOf<MoviesUiState>()
        val job = testScope.launch {
            viewModel.moviesStateFlow.collect { emissions.add(it) }
        }

        coEvery { useCase.GetPopularMovies() } coAnswers {
            delay(10)
            listOf(defaultGoodMovie, defaultBadMovie)
        }

        viewModel.getAllMovies()

        assertEquals(true,emissions.any { it.isLoading })

        advanceUntilIdle()

        val final = emissions.last()
        assertEquals(false,final.isLoading)
        assertEquals(listOf(defaultGoodMovie, defaultBadMovie), final.movies)
        job.cancel()
    }
}