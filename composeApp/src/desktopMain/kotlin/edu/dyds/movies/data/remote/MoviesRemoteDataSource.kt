package edu.dyds.movies.data.remote


interface MoviesRemoteDataSource {
    suspend fun getPopularMovies(): RemoteResult

    suspend fun getMovieDetails(id: Int): RemoteMovie
}