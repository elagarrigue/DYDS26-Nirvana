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
    fun `dado que no se guardan películas, al consultar cache retona lista vacía`() = runTest {
        assertTrue(localDataSource.getPopularMoviesFromCache().isEmpty())
    }
    @Test
    fun `dada una cache vacía, al guardar películas, la cache las contiene`() = runTest {
        val movies = listOf(default.copy(id = 1), default.copy(id = 2))

        localDataSource.savePopularMovies(movies)

        assertEquals(movies, localDataSource.getPopularMoviesFromCache())
    }

    @Test
    fun `guardar nuevas peliculas, entonces se reemplaza el cache anterior`() = runTest {
        localDataSource.savePopularMovies(listOf(default.copy(id = 1), default.copy(id = 2)))
        val secondBatch = listOf(default.copy(id = 3), default.copy(id = 4))

        localDataSource.savePopularMovies(secondBatch)

        assertEquals(secondBatch, localDataSource.getPopularMoviesFromCache())
    }
    @Test
    fun `guardar una lista vacia en cache, entonces estara vacia la cache`() = runTest {
        localDataSource.savePopularMovies(listOf(default.copy(id = 1)))

        localDataSource.savePopularMovies(emptyList())

        assertTrue(localDataSource.getPopularMoviesFromCache().isEmpty())
    }
    @Test
    fun `en una cache con varias películas, al buscar por un id existente, retorna la película correcta`() = runTest {
        val targetMovie = default.copy(id = 42, title = "Inception")
        localDataSource.savePopularMovies(listOf(default.copy(id = 1), targetMovie, default.copy(id = 3)))

        val result = localDataSource.getMovieDetailFromCache(42)

        assertEquals(targetMovie, result)
    }
    @Test
    fun `dado un id que no existe en cache, al buscar por ese id, retorna null`() = runTest {
        localDataSource.savePopularMovies(listOf(default.copy(id = 1), default.copy(id = 2)))

        val result = localDataSource.getMovieDetailFromCache(99)

        assertNull(result)
    }
    @Test
    fun `dada una cache vacia, una busqueda de descripcion, retorna null`() = runTest {
        val result = localDataSource.getMovieDetailFromCache(1)

        assertNull(result)
    }

}