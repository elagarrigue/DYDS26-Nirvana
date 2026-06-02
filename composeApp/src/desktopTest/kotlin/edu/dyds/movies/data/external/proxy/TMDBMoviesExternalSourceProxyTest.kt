package edu.dyds.movies.data.external.proxy

import edu.dyds.movies.data.external.tmdb.RemoteResult
import edu.dyds.movies.data.external.tmdb.RemoteTMDB
import edu.dyds.movies.data.external.tmdb.TMDBMoviesExternalSource
import edu.dyds.movies.domain.entity.Movie
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TMDBMoviesExternalSourceProxyTest {

    @Test
    fun `getPopularMovies convierte paths relativos a URLs completas y preserva valores vacios`() = runTest {
        val remoteSource = mockk<TMDBMoviesExternalSource>()
        val remoteMovies = listOf(
            createRemoteMovie(id = 1, title = "Movie 1", posterPath = "/poster.jpg", backdropPath = "/backdrop.jpg"),
            createRemoteMovie(id = 2, title = "Movie 2", posterPath = null, backdropPath = null)
        )
        coEvery { remoteSource.getPopularMovies() } returns RemoteResult(
            page = 1,
            results = remoteMovies,
            totalPages = 1,
            totalResults = 2
        )

        val proxy = TMDBMoviesExternalSourceProxy(remoteSource)

        val result = proxy.getPopularMovies()

        assertEquals(
            listOf(
                movie(
                    id = 1,
                    title = "Movie 1",
                    poster = "https://image.tmdb.org/t/p/w500/poster.jpg",
                    backdrop = "https://image.tmdb.org/t/p/w780/backdrop.jpg"
                ),
                movie(
                    id = 2,
                    title = "Movie 2",
                    poster = "",
                    backdrop = ""
                )
            ),
            result
        )
    }

    @Test
    fun `getMovieByTitle preserva URLs absolutas de imagen y mapea detalle remoto`() = runTest {
        val remoteSource = mockk<TMDBMoviesExternalSource>()
        val remoteMovie = createRemoteMovie(
            id = 7,
            title = "Inception",
            posterPath = "https://cdn.example.com/poster.jpg",
            backdropPath = "https://cdn.example.com/backdrop.jpg"
        )
        coEvery { remoteSource.getMovieByTitle("Inception") } returns remoteMovie

        val proxy = TMDBMoviesExternalSourceProxy(remoteSource)

        val result = proxy.getMovieByTitle("Inception")

        assertEquals(
            movie(
                id = 7,
                title = "Inception",
                poster = "https://cdn.example.com/poster.jpg",
                backdrop = "https://cdn.example.com/backdrop.jpg"
            ),
            result
        )
    }

    @Test
    fun `getMovieByTitle devuelve null si el data source no encuentra coincidencia`() = runTest {
        val remoteSource = mockk<TMDBMoviesExternalSource>()
        coEvery { remoteSource.getMovieByTitle("Unknown") } returns null

        val proxy = TMDBMoviesExternalSourceProxy(remoteSource)

        assertNull(proxy.getMovieByTitle("Unknown"))
    }

    private fun createRemoteMovie(
        id: Int,
        title: String,
        overview: String = "Overview",
        releaseDate: String = "2024-01-01",
        posterPath: String? = "/poster.jpg",
        backdropPath: String? = "/backdrop.jpg",
        originalTitle: String = title,
        originalLanguage: String = "en",
        popularity: Double = 8.0,
        voteAverage: Double = 7.5
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

    private fun movie(
        id: Int,
        title: String,
        overview: String = "Overview",
        releaseDate: String = "2024-01-01",
        poster: String,
        backdrop: String?,
        originalTitle: String = title,
        originalLanguage: String = "en",
        popularity: Double = 8.0,
        voteAverage: Double = 7.5
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
