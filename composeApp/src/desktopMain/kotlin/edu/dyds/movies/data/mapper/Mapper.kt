package edu.dyds.movies.data.mapper

import edu.dyds.movies.domain.entity.Movie

/**
 * Interfaz genérica para mapear entre tipos de datos
 */
interface Mapper<FROM, TO> {
    fun map(input: FROM): TO
    fun mapList(input: List<FROM>): List<TO> = input.map { map(it) }
}

