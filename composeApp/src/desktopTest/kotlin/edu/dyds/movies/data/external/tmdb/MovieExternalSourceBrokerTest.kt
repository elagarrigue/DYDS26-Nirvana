package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.MovieExternalSourceBroker
import edu.dyds.movies.domain.entity.Movie
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieExternalSourceBrokerTest {

    private lateinit var tmdbSource: MovieExternalSource
    private lateinit var omdbSource: MovieExternalSource
    private lateinit var broker: MovieExternalSourceBroker

    private fun setupBroker(
        tmdbSource: MovieExternalSource = mockk(),
        omdbSource: MovieExternalSource = mockk()
    ) {
        this.tmdbSource = tmdbSource
        this.omdbSource = omdbSource
        this.broker = MovieExternalSourceBroker(tmdbSource, omdbSource)
    }


    @Test
    fun `si la busqueda de pelicula por TMDB tiene exito y por OMBD falla, getMovieByTitle debe devolver el resultado de TMDB`() = runTest {
        val tmdbMovie = createMovie(title = "Inception")
        setupBroker(
            tmdbSource = mockk {
                coEvery { getMovieByTitle("Inception") } returns tmdbMovie
            },
            omdbSource = mockk {
                coEvery { getMovieByTitle("Inception") } returns null
            }
        )

        val result = broker.getMovieByTitle("Inception")

        val expected = tmdbMovie.copy(overview = "TMDB: ${tmdbMovie.overview}")
        assertEquals(expected, result)
    }

    @Test
    fun `si la busqueda de pelicula por OMBD tiene exito y por TMBD falla, getMovieByTitle debe devolver el resultado de OMDB`() = runTest {
        val omdbMovie = createMovie(title = "Inception", overview = "OMDB synopsis")
        setupBroker(
            tmdbSource = mockk {
                coEvery { getMovieByTitle("Inception") } returns null
            },
            omdbSource = mockk {
                coEvery { getMovieByTitle("Inception") } returns omdbMovie
            }
        )

        val result = broker.getMovieByTitle("Inception")

        val expected = omdbMovie.copy(overview = "OMDB: ${omdbMovie.overview}")
        assertEquals(expected, result)
    }

    @Test
    fun `si la busqueda de pelicula por TMDB tiene exito y por OMBD tiene exito, getMovieByTitle debe devolver una fusion de ambas`() = runTest {
        val tmdbMovie = createMovie(
            title = "Inception",
            overview = "TMDB overview",
            popularity = 8.5,
            voteAverage = 8.8
        )
        val omdbMovie = createMovie(
            title = "Inception",
            overview = "OMDB overview",
            popularity = 7.5,
            voteAverage = 8.4
        )

        setupBroker(
            tmdbSource = mockk {
                coEvery { getMovieByTitle("Inception") } returns tmdbMovie
            },
            omdbSource = mockk {
                coEvery { getMovieByTitle("Inception") } returns omdbMovie
            }
        )

        val result = requireNotNull(broker.getMovieByTitle("Inception"))

        val expectedOverview = "TMDB: TMDB overview\n\nOMDB: OMDB overview"
        val expectedPopularity = 8.0
        val expectedVoteAverage = 8.6

        assertEquals(expectedOverview, result.overview)
        assertEquals(expectedPopularity, result.popularity, 0.001)
        assertEquals(expectedVoteAverage, result.voteAverage, 0.001)
    }

    @Test
    fun `si la busqueda de pelicula por TMDB falla y por OMDB falla, getMovieByTitle devuelve null`() = runTest {
        setupBroker(
            tmdbSource = mockk {
                coEvery { getMovieByTitle("Unknown") } returns null
            },
            omdbSource = mockk {
                coEvery { getMovieByTitle("Unknown") } returns null
            }
        )

        val result = broker.getMovieByTitle("Unknown")
        assertNull(result)
    }

    @Test
    fun `si la busqueda de pelicula por TMDB devuelve null y por OMDB devuelve null, getMovieByTitle devuelve null`() = runTest {
        setupBroker(
            tmdbSource = mockk {
                coEvery { getMovieByTitle("Unknown") } returns null
            },
            omdbSource = mockk {
                coEvery { getMovieByTitle("Unknown") } returns null
            }
        )

        val result = broker.getMovieByTitle("Unknown")

        assertNull(result)
    }

    private fun createMovie(
        id: Int = 1,
        title: String = "Test Movie",
        overview: String = "Test Overview",
        releaseDate: String = "2023-01-01",
        poster: String = "/poster.jpg",
        backdrop: String? = "/backdrop.jpg",
        originalTitle: String = "Test Movie",
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