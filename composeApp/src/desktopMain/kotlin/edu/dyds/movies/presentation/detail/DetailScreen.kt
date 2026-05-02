@file:Suppress("FunctionName")

package edu.dyds.movies.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dydsproject.composeapp.generated.resources.*
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.presentation.detail.MovieDetailUiState
import org.jetbrains.compose.resources.stringResource
import edu.dyds.movies.presentation.utils.LoadingIndicator
import edu.dyds.movies.presentation.utils.NoResults

private val DetailContentPadding = 16.dp
private const val DetailImageAspectRatio = 16f / 9f
private val DetailLineHeight = 18.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: DetailViewModel, id: Int, onBack: () -> Unit) {

    val state by viewModel.movieDetailStateFlow.collectAsState(MovieDetailUiState())

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(id) {
        viewModel.getMovieDetail(id)
    }

    MaterialTheme {
        Surface {
            Scaffold(
                topBar = {
                    DetailTopBar(
                        title = state.movie?.title ?: "",
                        onBack = onBack,
                        scrollBehavior = scrollBehavior
                    )
                }
            ) { padding ->

                LoadingIndicator(enabled = state.isLoading, modifier = Modifier.padding(padding))

                state.movie?.let { movie ->
                    MovieDetailContent(movie = movie, modifier = Modifier.padding(padding))
                } ?: run {
                    if (!state.isLoading) {
                        NoResults { viewModel.getMovieDetail(id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieDetailContent(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        MovieBackdrop(movie)
        MovieOverview(movie.overview)
        MovieDetailMetadata(movie)
    }
}

@Composable
private fun MovieBackdrop(movie: Movie) {
    AsyncImage(
        model = movie.backdrop ?: movie.poster,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(DetailImageAspectRatio),
    )
}

@Composable
private fun MovieOverview(overview: String) {
    Text(
        text = overview,
        modifier = Modifier.padding(DetailContentPadding),
    )
}

@Composable
private fun MovieDetailMetadata(movie: Movie) {
    val metadata = listOf(
        stringResource(Res.string.original_language) to movie.originalLanguage,
        stringResource(Res.string.original_title) to movie.originalTitle,
        stringResource(Res.string.popularity) to movie.popularity.toString(),
        stringResource(Res.string.release_date) to movie.releaseDate,
        stringResource(Res.string.vote_average) to movie.voteAverage.toString()
    )

    Text(
        text = buildAnnotatedString {
            metadata.forEachIndexed { index, (name, value) ->
                appendMovieProperty(name, value, end = index == metadata.lastIndex)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(DetailContentPadding),
    )
}

private fun AnnotatedString.Builder.appendMovieProperty(name: String, value: String, end: Boolean = false) {
    withStyle(ParagraphStyle(lineHeight = DetailLineHeight)) {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("$name: ")
        }
        append(value)
        if (!end) {
            append("\n")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar(
    title: String,
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}
