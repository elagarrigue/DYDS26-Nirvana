package edu.dyds.movies.data.external.proxy

import edu.dyds.movies.data.external.omdb.OMDBMoviesExternalSource
import edu.dyds.movies.data.external.omdb.RemoteOMDB
import edu.dyds.movies.domain.entity.Movie
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class OMDBMoviesExternalSourceProxyTest {

    @Test
    fun `getMovieByTitle convierte el remoto de OMDB a Movie y normaliza ratings invalidos`() = runTest {
        val remoteSource = mockk<OMDBMoviesExternalSource>()
        val remoteMovie = RemoteOMDB(
            title = "Inception",
            plot = "OMDB plot",
            released = "N/A",
            year = "2010",
            poster = "https://poster.jpg",
            language = "English",
            metaScore = "N/A",
            imdbRating = "N/A"
        )
        coEvery { remoteSource.getMovieByTitle("Inception") } returns remoteMovie

        val proxy = OMDBMoviesExternalSourceProxy(remoteSource)

        val result = proxy.getMovieByTitle("Inception")

        assertEquals(
            Movie(
                id = "Inception".hashCode(),
                title = "Inception",
                overview = "OMDB plot",
                releaseDate = "2010",
                poster = "https://poster.jpg",
                backdrop = "https://poster.jpg",
                originalTitle = "Inception",
                originalLanguage = "English",
                popularity = 0.0,
                voteAverage = 0.0
            ),
            result
        )
    }
}
