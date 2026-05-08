package edu.dyds.movies.commonFakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie

fun fakeMovie(
    id: Int = 1,
    title: String = "Fake Movie",
    overview: String = "A fake overview",
    releaseDate: String = "2025-10-31",
    poster: String = "/fake_poster.jpg",
    backdrop: String? = "/fake_backdrop.jpg",
    originalTitle: String = "Fake Movie Original",
    originalLanguage: String = "en",
    popularity: Double = 7.5,
    voteAverage: Double = 8.0
) = Movie(
    id = id,
    title = title,
    overview = overview,
    releaseDate = releaseDate,
    poster = poster,
    backdrop = backdrop,
    originalTitle = originalTitle,
    originalLanguage = originalLanguage,
    popularity = popularity,
    voteAverage = voteAverage
)

fun fakeQualifiedMovie(
    movie: Movie = fakeMovie(),
    isGoodMovie: Boolean = true
) = QualifiedMovie(movie, isGoodMovie)