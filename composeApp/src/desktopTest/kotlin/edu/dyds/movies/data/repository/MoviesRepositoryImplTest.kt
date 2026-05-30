package edu.dyds.movies.data.repository

import edu.dyds.movies.commonFakes.FakeMovieExternalSource
import edu.dyds.movies.commonFakes.FakeMoviesExternalSource
import edu.dyds.movies.commonFakes.FakeMoviesLocalDataSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class MoviesRepositoryImplTest {
    private lateinit var movieExternalSource: FakeMovieExternalSource
    private lateinit var moviesExternalSource: FakeMoviesExternalSource
    private lateinit var localDataSource: FakeMoviesLocalDataSource
    private lateinit var repository: MoviesRepositoryImpl

    @BeforeTest
    fun setup() {
        movieExternalSource = FakeMovieExternalSource()
        moviesExternalSource = FakeMoviesExternalSource()
        localDataSource = FakeMoviesLocalDataSource()
        repository = MoviesRepositoryImpl(
            movieExternalSource = movieExternalSource,
            moviesExternalSource = moviesExternalSource,
            localDataSource = localDataSource
        )
    }

    @Test
    fun `teniendo películas en cache, al llamar a getPopularMovies, se devuelven las películas en cache sin consultar remoto`() = runBlocking {
        val cachedMovies = listOf(Movie(1, "title", "overview", "2020-01-01", "poster", null, "originalTitle", "en", 1.0, 8.0))
        localDataSource.cachedMovies = cachedMovies
        val result = repository.getPopularMovies()
        assertEquals(cachedMovies, result)
    }

    @Test
    fun `realiza búsquedas desde el servidor remoto y almacena en caché, si la caché está vacía`() = runBlocking {
        localDataSource.cachedMovies = emptyList()
        val remoteMovie = Movie(1, "title", "overview", "2020-01-01", "poster", null, "originalTitle", "en", 1.0, 8.0)
        moviesExternalSource.remoteMovies = listOf(remoteMovie)
        val result = repository.getPopularMovies()
        assertEquals(1, result.size)
        assertEquals("title", result[0].title)
        assertEquals(remoteMovie.id, result[0].id)
        assertEquals(listOf(remoteMovie), localDataSource.savedMovies)
    }

    @Test
    fun `dado que remoto tiene datos de la película, al llamar a getMovieByTitle, devuelve los detalles de la película desde remote si están disponibles`() = runBlocking {
        val remoteMovie = Movie(2, "title2", "overview2", "2020-02-02", "poster2", null, "originalTitle2", "es", 2.0, 7.0)
        movieExternalSource.remoteMovie = remoteMovie
        val result = repository.getMovieByTitle("title2")
        assertNotNull(result)
        assertEquals(remoteMovie, result)
    }

    @Test
    fun `en caso de error remoto, se recurre a la caché local`() = runBlocking {
        movieExternalSource.shouldThrow = true
        val cachedMovie = Movie(3, "title3", "overview3", "2020-03-03", "poster3", null, "originalTitle3", "fr", 3.0, 6.0)
        localDataSource.cachedMovies = listOf(cachedMovie)
        val result = repository.getMovieByTitle("title3")
        assertEquals(cachedMovie, result)
    }

    @Test
    fun `si falla omdb y la cache esta vacia, getMovieByTitle devuelve null`() = runBlocking {
        movieExternalSource.shouldThrow = true
        localDataSource.cachedMovies = emptyList()

        val result = repository.getMovieByTitle("Unknown")

        assertNull(result)
    }
}
