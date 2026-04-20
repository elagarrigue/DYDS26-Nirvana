package edu.dyds.movies.data.mapper

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.RemoteMovie

/**
 * Extension functions para mapeo de RemoteMovie a Movie (dominio)
 */
fun RemoteMovie.toDomainMovie(): Movie = RemoteMovieToDomainMapper().map(this)

fun List<RemoteMovie>.toDomainMovies(): List<Movie> = RemoteMovieToDomainMapper().mapList(this)

