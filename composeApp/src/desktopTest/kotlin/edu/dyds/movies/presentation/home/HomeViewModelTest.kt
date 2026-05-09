package edu.dyds.movies.presentation.home

import edu.dyds.movies.commonFakes.FakeGetPopularMoviesUseCase
import edu.dyds.movies.commonFakes.fakeQualifiedMovie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var fakeUseCase: FakeGetPopularMoviesUseCase
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        fakeUseCase = FakeGetPopularMoviesUseCase()
        viewModel = HomeViewModel(fakeUseCase)
    }

    @Test
    fun `emits loading then movies when movies are found`() = runTest {
        val movies = listOf(
            fakeQualifiedMovie(isGoodMovie = true),
            fakeQualifiedMovie(isGoodMovie = false)
        )
        fakeUseCase.moviesToReturn = movies

        viewModel.getAllMovies()

        val state = viewModel.moviesStateFlow.first { !it.isLoading }
        assertEquals(movies, state.movies)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `emits loading then empty list when no movies are found`() = runTest {
        fakeUseCase.moviesToReturn = emptyList()

        viewModel.getAllMovies()

        val state = viewModel.moviesStateFlow.first { !it.isLoading }
        assertEquals(emptyList<Any>(), state.movies)
        assertEquals(false, state.isLoading)
    }
}
