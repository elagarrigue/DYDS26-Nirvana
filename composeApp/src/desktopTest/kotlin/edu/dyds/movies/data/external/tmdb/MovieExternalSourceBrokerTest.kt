package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.MovieExternalSourceBroker
import edu.dyds.movies.data.external.proxy.OMDBMoviesExternalSourceProxy
import edu.dyds.movies.data.external.proxy.TMDBMoviesExternalSourceProxy
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieExternalSourceBrokerTest {

    private lateinit var tmdbProxy: TMDBMoviesExternalSourceProxy
    private lateinit var omdbProxy: OMDBMoviesExternalSourceProxy
    private lateinit var broker: MovieExternalSourceBroker

    private fun setupBroker(
        tmdbProxy: TMDBMoviesExternalSourceProxy = mockk(),
        omdbProxy: OMDBMoviesExternalSourceProxy = mockk()
    ) {
        this.tmdbProxy = tmdbProxy
        this.omdbProxy = omdbProxy
        this.broker = MovieExternalSourceBroker(tmdbProxy, omdbProxy)
    }


    @Test
    fun `si la busqueda de pelicula por TMDB tiene exito y por OMBD falla, getMovieByTitle debe devolver el resultado de TMDB`() = runTest {
        val tmdbMovie = createRemoteTMDB(title = "Inception")
        setupBroker(
            tmdbProxy = mockk {
                coEvery { getMovieByTitle("Inception") } returns tmdbMovie
            },
            omdbProxy = mockk {
                coEvery { getMovieByTitle("Inception") } throws Exception("OMDB error")
            }
        )

        val result = broker.getMovieByTitle("Inception")

        val expected = tmdbMovie.copy(overview = "TMDB: ${tmdbMovie.overview}")
        assertEquals(expected, result)
    }

    @Test
    fun `si la busqueda de pelicula por OMBD tiene exito y por TMBD falla, getMovieByTitle debe devolver el resultado de OMDB`() = runTest {
        val omdbMovie = createRemoteTMDB(title = "Inception", overview = "OMDB synopsis")
        setupBroker(
            tmdbProxy = mockk {
                coEvery { getMovieByTitle("Inception") } throws Exception("TMDB error")
            },
            omdbProxy = mockk {
                coEvery { getMovieByTitle("Inception") } returns omdbMovie
            }
        )

        val result = broker.getMovieByTitle("Inception")

        val expected = omdbMovie.copy(overview = "OMDB: ${omdbMovie.overview}")
        assertEquals(expected, result)
    }

    @Test
    fun `si la busqueda de pelicula por TMDB tiene exito y por OMBD tiene exito, getMovieByTitle debe devolver una fusion de ambas`() = runTest {
        val tmdbMovie = createRemoteTMDB(
            title = "Inception",
            overview = "TMDB overview",
            popularity = 8.5,
            voteAverage = 8.8
        )
        val omdbMovie = createRemoteTMDB(
            title = "Inception",
            overview = "OMDB overview",
            popularity = 7.5,
            voteAverage = 8.4
        )

        setupBroker(
            tmdbProxy = mockk {
                coEvery { getMovieByTitle("Inception") } returns tmdbMovie
            },
            omdbProxy = mockk {
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
            tmdbProxy = mockk {
                coEvery { getMovieByTitle("Unknown") } throws Exception("TMDB error")
            },
            omdbProxy = mockk {
                coEvery { getMovieByTitle("Unknown") } throws Exception("OMDB error")
            }
        )

        val result = broker.getMovieByTitle("Unknown")
        assertNull(result)
    }

    private fun createRemoteTMDB(
        id: Int = 1,
        title: String = "Test Movie",
        overview: String = "Test Overview",
        releaseDate: String = "2023-01-01",
        posterPath: String? = "/poster.jpg",
        backdropPath: String? = "/backdrop.jpg",
        originalTitle: String = "Test Movie",
        originalLanguage: String = "en",
        popularity: Double = 8.0,
        voteAverage: Double = 8.0
    ): RemoteTMDB = RemoteTMDB(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        posterPath = posterPath,
        backdropPath = backdropPath,
        originalTitle = originalTitle,
        originalLanguage = originalLanguage,
        popularity = popularity,
        voteAverage = voteAverage
    )
}