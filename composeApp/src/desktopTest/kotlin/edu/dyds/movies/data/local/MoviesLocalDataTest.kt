package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class MoviesLocalDataTest {

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

    private lateinit var localDataSource: MoviesLocalDataSourceImpl

    @Before
    fun setUp() {
        localDataSource = MoviesLocalDataSourceImpl()
    }
    @Test
    fun `cache se crea vacia`() = runTest {
        assertTrue(localDataSource.getPopularMoviesFromCache().isEmpty())
    }
    @Test
    fun `guardado de peliculas en cache`() = runTest {
        val movies = listOf(default.copy(id = 1), default.copy(id = 2))

        localDataSource.savePopularMovies(movies)

        assertEquals(movies, localDataSource.getPopularMoviesFromCache())
    }

    @Test
    fun `guardar nuevas peliculas reemplaza el cache anterior`() = runTest {
        localDataSource.savePopularMovies(listOf(default.copy(id = 1), default.copy(id = 2)))
        val secondBatch = listOf(default.copy(id = 3), default.copy(id = 4))

        localDataSource.savePopularMovies(secondBatch)

        assertEquals(secondBatch, localDataSource.getPopularMoviesFromCache())
    }
    @Test
    fun `guardar una lista vacia en cache`() = runTest {
        localDataSource.savePopularMovies(listOf(default.copy(id = 1)))

        localDataSource.savePopularMovies(emptyList())

        assertTrue(localDataSource.getPopularMoviesFromCache().isEmpty())
    }
    @Test
    fun `buscar pelicula por id`() = runTest {
        val targetMovie = default.copy(id = 42, title = "Inception")
        localDataSource.savePopularMovies(listOf(default.copy(id = 1), targetMovie, default.copy(id = 3)))

        val result = localDataSource.getMovieDetailFromCache(42)

        assertEquals(targetMovie, result)
    }
    @Test
    fun `busqueda de id inexistente`() = runTest {
        localDataSource.savePopularMovies(listOf(default.copy(id = 1), default.copy(id = 2)))

        val result = localDataSource.getMovieDetailFromCache(99)

        assertNull(result)
    }
    @Test
    fun `busqueda de descripcion con cache vacia`() = runTest {
        val result = localDataSource.getMovieDetailFromCache(1)

        assertNull(result)
    }

}