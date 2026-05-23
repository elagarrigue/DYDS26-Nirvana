package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.tmdb.RemoteTMDB as TmdbRemoteMovie
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RemoteOMDB (
    @SerialName("Title") val title: String,
    @SerialName("Plot") val plot : String ,
    @SerialName("Released") val  released : String ,
    @SerialName("Year") val year : String ,
    @SerialName("Poster") val poster : String ,
    @SerialName("Language") val language : String ,
    @SerialName("Metascore") val  metaScore : String ,
    val imdbRating: String,
) {
    fun toDomainMovie(): TmdbRemoteMovie {
        val imdb = imdbRating
            .takeIf { it.isNotEmpty() && it != "N/A" }
            ?.toDoubleOrNull() ?: 0.0
        val vote = if (metaScore.isNotEmpty() && metaScore != "N/A") metaScore.toDouble() else 0.0

        return TmdbRemoteMovie(
            id = title.hashCode(),
            title = title,
            overview = plot,
            releaseDate = if (released.isNotEmpty() && released != "N/A") released else year,
            posterPath = poster,
            backdropPath = poster,
            originalTitle = title,
            originalLanguage = language,
            popularity = imdb,
            voteAverage = if (metaScore.isNotEmpty() && metaScore != "N/A") metaScore.toDouble() else 0.0,
        )
    }
}
