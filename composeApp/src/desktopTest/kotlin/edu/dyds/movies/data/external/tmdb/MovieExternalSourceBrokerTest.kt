package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.MovieExternalSourceBroker
import edu.dyds.movies.domain.entity.Movie
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MovieExternalSourceBrokerTest {

    @Test
    fun `si TMDB y OMDB devuelven pelicula fusiona overview y promedia puntuaciones`() = runTest {
        val tmdbSource = mockk<MovieDetailExternalSource>()
        val omdbSource = mockk<MovieDetailExternalSource>()
        val tmdbMovie = movie(title = "Inception", overview = "TMDB overview", popularity = 8.5, voteAverage = 8.8)
        val omdbMovie = movie(title = "Inception", overview = "OMDB overview", popularity = 7.5, voteAverage = 8.4)

        coEvery { tmdbSource.getMovieByTitle("Inception") } returns tmdbMovie
        coEvery { omdbSource.getMovieByTitle("Inception") } returns omdbMovie

        val broker = MovieExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieByTitle("Inception")

        assertNotNull(result)
        assertEquals(tmdbMovie.id, result.id)
        assertEquals(tmdbMovie.title, result.title)
        assertEquals("TMDB: TMDB overview\n\nOMDB: OMDB overview", result.overview)
        assertEquals(8.0, result.popularity, 0.0001)
        assertEquals(8.6, result.voteAverage, 0.0001)
    }

    @Test
    fun `si TMDB devuelve pelicula y OMDB falla conserva TMDB con prefijo de overview`() = runTest {
        val tmdbSource = mockk<MovieDetailExternalSource>()
        val omdbSource = mockk<MovieDetailExternalSource>()
        val tmdbMovie = movie(title = "Inception", overview = "TMDB overview")

        coEvery { tmdbSource.getMovieByTitle("Inception") } returns tmdbMovie
        coEvery { omdbSource.getMovieByTitle("Inception") } throws java.io.IOException("boom")

        val broker = MovieExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieByTitle("Inception")

        assertEquals(tmdbMovie.copy(overview = "TMDB: TMDB overview"), result)
    }

    @Test
    fun `si TMDB falla y OMDB devuelve pelicula conserva OMDB con prefijo de overview`() = runTest {
        val tmdbSource = mockk<MovieDetailExternalSource>()
        val omdbSource = mockk<MovieDetailExternalSource>()
        val omdbMovie = movie(title = "Inception", overview = "OMDB overview")

        coEvery { tmdbSource.getMovieByTitle("Inception") } throws java.io.IOException("boom")
        coEvery { omdbSource.getMovieByTitle("Inception") } returns omdbMovie

        val broker = MovieExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieByTitle("Inception")

        assertEquals(omdbMovie.copy(overview = "OMDB: OMDB overview"), result)
    }

    @Test
    fun `si ambos fallan devuelve null`() = runTest {
        val tmdbSource = mockk<MovieDetailExternalSource>()
        val omdbSource = mockk<MovieDetailExternalSource>()

        coEvery { tmdbSource.getMovieByTitle("Unknown") } throws java.io.IOException("boom")
        coEvery { omdbSource.getMovieByTitle("Unknown") } returns null

        val broker = MovieExternalSourceBroker(tmdbSource, omdbSource)

        assertNull(broker.getMovieByTitle("Unknown"))
    }

    private fun movie(
        id: Int = 1,
        title: String = "Test Movie",
        overview: String = "Test Overview",
        releaseDate: String = "2023-01-01",
        poster: String = "https://poster.jpg",
        backdrop: String? = "https://backdrop.jpg",
        originalTitle: String = title,
        originalLanguage: String = "en",
        popularity: Double = 8.0,
        voteAverage: Double = 8.0
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
