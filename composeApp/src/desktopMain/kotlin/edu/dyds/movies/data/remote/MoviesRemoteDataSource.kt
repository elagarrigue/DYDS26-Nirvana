package edu.dyds.movies.data.remote


interface MoviesRemoteDataSource {
    suspend fun getPopularMovies(): RemoteResult

    suspend fun getMovieByTitle(title: String): RemoteMovie
}
