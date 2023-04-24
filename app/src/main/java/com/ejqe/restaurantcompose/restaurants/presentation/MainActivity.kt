package com.ejqe.restaurantcompose.restaurants.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.ejqe.restaurantcompose.restaurants.presentation.details.RestaurantDetailsScreen
import com.ejqe.restaurantcompose.restaurants.presentation.list.RestaurantsScreen
import com.ejqe.restaurantcompose.restaurants.presentation.list.RestaurantsViewModel
import com.ejqe.restaurantcompose.ui.theme.RestaurantComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RestaurantComposeTheme {
//                RestaurantsScreen()
                RestaurantApp()
            }
        }
    }


    @Composable
    fun RestaurantApp() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "restaurants") {
            composable(route = "restaurants") {
                val viewModel: RestaurantsViewModel = viewModel()
                RestaurantsScreen(
                    state = viewModel.state.value,
                    onItemClick = { id -> navController.navigate("restaurants/$id")},
                    onFavoriteClick = { id, oldValue -> viewModel.toggleFavorite(id, oldValue)}
                )
            }
            composable(
                route = "restaurants/{restaurant_id}",
                arguments = listOf(navArgument("restaurant_id") {
                    type = NavType.IntType
                }),
                deepLinks = listOf(navDeepLink {
                    uriPattern = "www.restaurantapp.details.com/{restaurant_id}"
                })
            ) { navStackEntry ->
                val id = navStackEntry.arguments?.getInt("restaurant_id")
                RestaurantDetailsScreen()
            }

        }
    }
}



