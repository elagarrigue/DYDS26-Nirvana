package edu.dyds.movies.data.local

import edu.dyds.movies.commonFakes.fakeMovie
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class MoviesLocalDataTest {

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
        val movies = listOf(fakeMovie(id = 1), fakeMovie(id = 2))

        localDataSource.savePopularMovies(movies)

        assertEquals(movies, localDataSource.getPopularMoviesFromCache())
    }

    @Test
    fun `sobreescribir pelicula si ya estaba en cache`() = runTest {
        localDataSource.savePopularMovies(listOf(fakeMovie(id = 1), fakeMovie(id = 2)))
        val secondBatch = listOf(fakeMovie(id = 3), fakeMovie(id = 4))

        localDataSource.savePopularMovies(secondBatch)

        assertEquals(secondBatch, localDataSource.getPopularMoviesFromCache())
    }
    @Test
    fun `guardar una lista vacia en cache vacia`() = runTest {
        localDataSource.savePopularMovies(listOf(fakeMovie(id = 1)))

        localDataSource.savePopularMovies(emptyList())

        assertTrue(localDataSource.getPopularMoviesFromCache().isEmpty())
    }
    @Test
    fun `buscar pelicula por id`() = runTest {
        val targetMovie = fakeMovie(id = 42, title = "Inception")
        localDataSource.savePopularMovies(listOf(fakeMovie(id = 1), targetMovie, fakeMovie(id = 3)))

        val result = localDataSource.getMovieDetailFromCache(42)

        assertEquals(targetMovie, result)
    }
    @Test
    fun `busqueda de id inexistente`() = runTest {
        localDataSource.savePopularMovies(listOf(fakeMovie(id = 1), fakeMovie(id = 2)))

        val result = localDataSource.getMovieDetailFromCache(99)

        assertNull(result)
    }
    @Test
    fun `busqueda de descripcion con cache vacia`() = runTest {
        val result = localDataSource.getMovieDetailFromCache(1)

        assertNull(result)
    }

}