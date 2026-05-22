package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.RemoteMovie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RemoteResult(
    val page: Int,
    val results: List<RemoteMovie>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)
