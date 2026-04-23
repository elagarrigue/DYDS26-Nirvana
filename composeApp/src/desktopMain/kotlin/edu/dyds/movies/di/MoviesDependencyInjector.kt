package edu.dyds.movies.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.dyds.movies.data.MoviesRepositoryImpl
import edu.dyds.movies.data.local.InMemoryMoviesLocalDataSource
import edu.dyds.movies.data.remote.TmdbMoviesRemoteDataSource
import edu.dyds.movies.domain.logicalUi.PopularMoviesViewModel
import edu.dyds.movies.domain.logicalUi.MovieDetailViewModel
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private const val API_KEY = "d18da1b5da16397619c688b0263cd281"

object MoviesDependencyInjector {

    private val tmdbHttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.themoviedb.org"
                    parameters.append("api_key", API_KEY)
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }

    private val remoteDataSource = TmdbMoviesRemoteDataSource(tmdbHttpClient)
    private val localDataSource = InMemoryMoviesLocalDataSource()
    private val moviesRepository = MoviesRepositoryImpl.create(remoteDataSource, localDataSource)

    @Composable
    fun getPopularMoviesViewModel(): PopularMoviesViewModel {
        return viewModel { PopularMoviesViewModel(moviesRepository) }
    }

    @Composable
    fun getMovieDetailViewModel(): MovieDetailViewModel {
        return viewModel { MovieDetailViewModel(moviesRepository) }
    }
}