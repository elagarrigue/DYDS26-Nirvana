@file:Suppress("FunctionName")

package edu.dyds.movies.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import dydsproject.composeapp.generated.resources.Res
import dydsproject.composeapp.generated.resources.app_name
import dydsproject.composeapp.generated.resources.error
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import org.jetbrains.compose.resources.stringResource
import edu.dyds.movies.presentation.utils.LoadingIndicator
import edu.dyds.movies.presentation.utils.NoResults

private val GridContentPadding = 4.dp
private val MovieTextPadding = 8.dp
private const val MovieImageAspectRatio = 2 / 3f
private const val DialogImageAspectRatio = 1f
private const val BadMovieAlpha = 0.7f
private val GridCellPadding = 120.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onGoodMovieClick: (Movie) -> Unit
) {

    val state by viewModel.moviesStateFlow.collectAsState(MoviesUiState())
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.getAllMovies()
    }

    MaterialTheme {
        Surface {
            Scaffold(
                topBar = { HomeTopBar(scrollBehavior) },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            ) { padding ->
                HomeScreenContent(
                    state = state,
                    onGoodMovieClick = onGoodMovieClick,
                    onRetry = { viewModel.getAllMovies() },
                    padding = padding
                )
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    state: MoviesUiState,
    onGoodMovieClick: (Movie) -> Unit,
    onRetry: () -> Unit,
    padding: PaddingValues
) {
    LoadingIndicator(state.isLoading)

    when {
        state.movies.isNotEmpty() -> MovieGrid(padding, state.movies, onGoodMovieClick)
        state.isLoading.not() -> NoResults(onRetry)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        { Text(stringResource(Res.string.app_name)) },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun MovieGrid(
    padding: PaddingValues,
    movies: List<QualifiedMovie>,
    onMovieClick: (Movie) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(GridCellPadding),
        contentPadding = PaddingValues(GridContentPadding),
        horizontalArrangement = Arrangement.spacedBy(GridContentPadding),
        verticalArrangement = Arrangement.spacedBy(GridContentPadding),
        modifier = Modifier.padding(padding)
    ) {
        items(movies, key = { it.movie.id }) { qualifiedMovie ->
            MovieGridItem(qualifiedMovie, onMovieClick)
        }
    }
}

@Composable
private fun MovieGridItem(
    qualifiedMovie: QualifiedMovie,
    onMovieClick: (Movie) -> Unit
) {
    when (qualifiedMovie.isGoodMovie) {
        true -> GoodMovieItem(qualifiedMovie.movie) { onMovieClick(qualifiedMovie.movie) }
        false -> BadMovieItem(qualifiedMovie.movie)
    }
}

@Composable
private fun MoviePosterImage(movie: Movie, modifier: Modifier = Modifier) {
    AsyncImage(
        model = movie.poster,
        contentDescription = movie.title,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(MovieImageAspectRatio)
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
private fun MovieTitle(movie: Movie) {
    Text(
        text = movie.title,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        modifier = Modifier.padding(MovieTextPadding)
    )
}

@Composable
private fun MovieCard(
    movie: Movie,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier
    ) {
        MoviePosterImage(movie)
        MovieTitle(movie)
    }
}

@Composable
private fun GoodMovieItem(movie: Movie, onClick: () -> Unit) {
    MovieCard(
        movie = movie,
        onClick = onClick
    )
}

@Composable
private fun BadMovieItem(movie: Movie) {
    var dialogState by remember { mutableStateOf(false) }

    MovieCard(
        movie = movie,
        modifier = Modifier.alpha(BadMovieAlpha),
        onClick = { dialogState = true }
    )

    BadMovieDialog(
        visible = dialogState,
        onCloseRequest = { dialogState = false }
    )
}

@Composable
private fun BadMovieDialog(
    visible: Boolean,
    onCloseRequest: () -> Unit
) {
    DialogWindow(
        title = stringResource(Res.string.error),
        resizable = false,
        onCloseRequest = onCloseRequest,
        visible = visible
    ) {
        Image(
            painter = painterResource("images/too_bad.png"),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(DialogImageAspectRatio)
        )
    }
}
