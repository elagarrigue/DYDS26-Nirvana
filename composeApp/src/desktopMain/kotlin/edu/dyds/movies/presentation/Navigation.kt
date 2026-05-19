@file:Suppress("FunctionName")

package edu.dyds.movies.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.dyds.movies.di.MoviesDependencyInjector
import edu.dyds.movies.presentation.detail.DetailScreen
import edu.dyds.movies.presentation.detail.DetailViewModel
import edu.dyds.movies.presentation.home.HomeScreen
import edu.dyds.movies.presentation.home.HomeViewModel

private const val HOME = "home"

private const val DETAIL = "detail"

private const val MOVIE_TITLE = "movieTitle"

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val detailViewModel = MoviesDependencyInjector.getDetailViewModel()
    val homeViewModel = MoviesDependencyInjector.getHomeViewModel()

    NavHost(navController = navController, startDestination = HOME) {
        homeDestination(navController, homeViewModel)
        detailDestination(navController, detailViewModel)
    }
}

private fun NavGraphBuilder.homeDestination(navController: NavHostController, moviesViewModel: HomeViewModel) {
    composable(HOME) {
        HomeScreen(
            viewModel = moviesViewModel,
            onGoodMovieClick = {
                navController.navigate("$DETAIL/${it.title}")
            }
        )
    }
}

private fun NavGraphBuilder.detailDestination(navController: NavHostController, moviesViewModel: DetailViewModel) {
    composable(
        route = "$DETAIL/{$MOVIE_TITLE}",
        arguments = listOf(navArgument(MOVIE_TITLE) { type = NavType.StringType })
    ) { backstackEntry ->
        val movieTitle = backstackEntry.arguments?.getString(MOVIE_TITLE)

        movieTitle?.let {
            DetailScreen(moviesViewModel, it, onBack = { navController.popBackStack() })
        }
    }
}
