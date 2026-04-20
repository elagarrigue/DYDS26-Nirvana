package edu.dyds.movies.data

import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.data.remote.MoviesRemoteDataSource
import edu.dyds.movies.data.remote.RemoteMoviesRepository
import edu.dyds.movies.domain.repository.MoviesRepository

/**
 * Factory class para crear la implementación default de [MoviesRepository].
 * Por defecto, retorna [RemoteMoviesRepository] que combina datos remotos con caché local.
 *
 * Para usar solo caché local (modo offline), inyecta [edu.dyds.movies.data.local.LocalMoviesRepository]
 */
class MoviesRepositoryImpl {
    companion object {
        fun create(
            remoteDataSource: MoviesRemoteDataSource,
            localDataSource: MoviesLocalDataSource
        ): MoviesRepository = RemoteMoviesRepository(remoteDataSource, localDataSource)
    }
}


