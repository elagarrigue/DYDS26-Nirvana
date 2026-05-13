package edu.dyds.movies.presentation.detail

import edu.dyds.movies.commonFakes.FakeGetMovieDetailsUseCase
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

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
    private lateinit var fakeUseCase: FakeGetMovieDetailsUseCase
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setUp() {
        fakeUseCase = FakeGetMovieDetailsUseCase()
        viewModel = DetailViewModel(fakeUseCase)
    }

    @Test
    fun `emite carga y luego película cuando se encuentra la película`() = runTest {
        fakeUseCase.movieToReturn = default

        viewModel.getMovieDetail(1)

        val state = viewModel.movieDetailStateFlow.first { !it.isLoading }
        assertEquals(default, state.movie)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `Emite carga y luego null cuando no se encuentra la película`() = runTest {
        fakeUseCase.movieToReturn = null

        viewModel.getMovieDetail(99)

        val state = viewModel.movieDetailStateFlow.first { !it.isLoading }
        assertNull(state.movie)
        assertEquals(false, state.isLoading)
    }
}
