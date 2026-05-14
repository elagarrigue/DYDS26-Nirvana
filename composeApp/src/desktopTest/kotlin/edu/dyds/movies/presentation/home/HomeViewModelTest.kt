package edu.dyds.movies.presentation.home

import edu.dyds.movies.commonFakes.FakeGetPopularMoviesUseCase
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
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
    private lateinit var fakeUseCase: FakeGetPopularMoviesUseCase
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        fakeUseCase = FakeGetPopularMoviesUseCase()
        viewModel = HomeViewModel(fakeUseCase)
    }

    @Test
    fun `emite carga y luego películas cuando se encuentran películas`() = runTest {
        val movies = listOf(
            defaultGoodMovie,
            defaultBadMovie
        )
        val continueUseCase = CompletableDeferred<Unit>()
        fakeUseCase.moviesToReturn = movies
        fakeUseCase.beforeReturning = { continueUseCase.await() }

        viewModel.getAllMovies()
        advanceUntilIdle()

        val loadingState = withTimeout(1_000) {
            viewModel.moviesStateFlow.first { it.isLoading }
        }
        assertEquals(true, loadingState.isLoading)
        assertEquals(emptyList<QualifiedMovie>(), loadingState.movies)

        continueUseCase.complete(Unit)
        advanceUntilIdle()

        val finalState = viewModel.moviesStateFlow.first { !it.isLoading }
        assertEquals(false, finalState.isLoading)
        assertEquals(movies, finalState.movies)
    }

    @Test
    fun `Emite un mensaje de carga y luego una lista vacía cuando no se encuentran películas`() = runTest {
        val continueUseCase = CompletableDeferred<Unit>()
        fakeUseCase.moviesToReturn = emptyList()
        fakeUseCase.beforeReturning = { continueUseCase.await() }

        viewModel.getAllMovies()
        advanceUntilIdle()

        val loadingState = withTimeout(1_000) {
            viewModel.moviesStateFlow.first { it.isLoading }
        }
        assertEquals(true, loadingState.isLoading)
        assertEquals(emptyList<QualifiedMovie>(), loadingState.movies)

        continueUseCase.complete(Unit)
        advanceUntilIdle()

        val finalState = viewModel.moviesStateFlow.first { !it.isLoading }
        assertEquals(false, finalState.isLoading)
        assertEquals(emptyList<QualifiedMovie>(), finalState.movies)
    }
}
