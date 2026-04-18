package edu.dyds.movies.domain.repository

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie

interface MoviesRepository {
    /**
     * Gets a list of popular movies, sorted and mapped as needed.
     * Should return domain entities only.
     */
    suspend fun getPopularMovies(): List<QualifiedMovie>

    /**
     * Gets the details for a specific movie by id.
     * Should return a domain entity or null if not found.
     */
    suspend fun getMovieDetail(id: Int): Movie?
}

