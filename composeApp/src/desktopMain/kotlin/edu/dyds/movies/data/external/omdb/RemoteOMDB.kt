package edu.dyds.movies.data.external.omdb

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
)
