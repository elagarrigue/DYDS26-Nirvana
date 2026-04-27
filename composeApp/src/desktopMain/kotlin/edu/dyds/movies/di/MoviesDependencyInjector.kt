package edu.dyds.movies.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.dyds.movies.domain.mapper.MovieMapper
import edu.dyds.movies.domain.qualifier.MovieQualifier
import edu.dyds.movies.data.repository.MoviesRepositoryImpl
import edu.dyds.movies.data.local.InMemoryMoviesLocalDataSource
import edu.dyds.movies.data.remote.TmdbMoviesRemoteDataSource
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import edu.dyds.movies.presentation.detailsviewmodel.DetailViewModel
import edu.dyds.movies.presentation.homeviewmodel.HomeViewModel
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

    // Capa de datos
    private val remoteDataSource = TmdbMoviesRemoteDataSource(tmdbHttpClient)
    private val localDataSource = InMemoryMoviesLocalDataSource()
    private val movieMapper = MovieMapper()
    private val movieQualifier = MovieQualifier

    private val moviesRepository = MoviesRepositoryImpl(
        remoteDataSource,
        localDataSource,
        movieMapper,
        movieQualifier
    )

    private val getMovieDetailsUseCase = GetMovieDetailsUseCase(moviesRepository)
    private val getPopularMoviesUseCase = GetPopularMoviesUseCase(moviesRepository)

    @Composable
    fun getDetailViewModel(): DetailViewModel {
        return viewModel { DetailViewModel(getMovieDetailsUseCase) }
    }

    @Composable
    fun getHomeViewModel() : HomeViewModel {
        return viewModel { HomeViewModel(getPopularMoviesUseCase) }
    }
}