package edu.dyds.movies.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.dyds.movies.data.remote.mapper.MovieMapper
import edu.dyds.movies.domain.qualifier.MovieQualifier
import edu.dyds.movies.data.repository.MoviesRepositoryImpl
import edu.dyds.movies.data.local.MoviesLocalDataSourceImpl
import edu.dyds.movies.data.remote.MoviesRemoteDataSourceImpl
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCaseImpl
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCaseImpl
import edu.dyds.movies.presentation.detail.DetailViewModel
import edu.dyds.movies.presentation.home.HomeViewModel
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
    private val remoteDataSource = MoviesRemoteDataSourceImpl(tmdbHttpClient)
    private val localDataSource = MoviesLocalDataSourceImpl()
    private val movieMapper = MovieMapper()
    private val movieQualifier = MovieQualifier()

    private val moviesRepository = MoviesRepositoryImpl(
        remoteDataSource,
        localDataSource,
        movieMapper
    )

    private val getMovieDetailsUseCase: GetMovieDetailsUseCase = GetMovieDetailsUseCaseImpl(moviesRepository)
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase = GetPopularMoviesUseCaseImpl(moviesRepository, movieQualifier)

    @Composable
    fun getDetailViewModel(): DetailViewModel {
        return viewModel { DetailViewModel(getMovieDetailsUseCase) }
    }

    @Composable
    fun getHomeViewModel() : HomeViewModel {
        return viewModel { HomeViewModel(getPopularMoviesUseCase) }
    }
}
