package edu.dyds.movies.presentation.home

import edu.dyds.movies.commonFakes.FakeGetPopularMoviesUseCase
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test@OptIn(ExperimentalCoroutinesApi::class)

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
    private lateinit var fakeUseCase: FakeGetPopularMoviesUseCase
    private lateinit var viewModel: HomeViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = CoroutineScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUseCase = FakeGetPopularMoviesUseCase()
        viewModel = HomeViewModel(fakeUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `dado use case con peliculas, cuando getAllMovies, emite loading y luego las peliculas`() = runTest {
        val events = ArrayList<MoviesUiState>()
        val movies = listOf<QualifiedMovie>(defaultBadMovie, defaultGoodMovie)

        testScope.launch {
            viewModel.moviesStateFlow.collect { events.add(it) }
            fakeUseCase.moviesToReturn = movies

            viewModel.getAllMovies()

            assertEquals(true, events[0].isLoading)
            assertEquals(movies, events[1].movies)
        }
    }


        @Test
        fun `Emite un mensaje de carga y luego una lista vacía cuando no se encuentran películas`() = runTest {
            val events = ArrayList<MoviesUiState>()

            testScope.launch { viewModel.moviesStateFlow.collect { events.add(it) } }

            fakeUseCase.moviesToReturn = emptyList()

            viewModel.getAllMovies()

            assertEquals(false, events[0].isLoading)
            assertEquals(emptyList<QualifiedMovie>(), events[0].movies)
        }
}