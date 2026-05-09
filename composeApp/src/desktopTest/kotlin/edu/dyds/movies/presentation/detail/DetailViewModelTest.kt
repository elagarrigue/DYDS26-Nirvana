package edu.dyds.movies.presentation.detail

import edu.dyds.movies.commonFakes.FakeGetMovieDetailsUseCase
import edu.dyds.movies.commonFakes.fakeMovie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private lateinit var fakeUseCase: FakeGetMovieDetailsUseCase
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setUp() {
        fakeUseCase = FakeGetMovieDetailsUseCase()
        viewModel = DetailViewModel(fakeUseCase)
    }

    @Test
    fun `emits loading then movie when movie is found`() = runTest {
        val movie = fakeMovie(id = 1, title = "Test Movie")
        fakeUseCase.movieToReturn = movie

        viewModel.getMovieDetail(1)

        val state = viewModel.movieDetailStateFlow.first { !it.isLoading }
        assertEquals(movie, state.movie)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `emits loading then null when movie is not found`() = runTest {
        fakeUseCase.movieToReturn = null

        viewModel.getMovieDetail(99)

        val state = viewModel.movieDetailStateFlow.first { !it.isLoading }
        assertNull(state.movie)
        assertEquals(false, state.isLoading)
    }
}
