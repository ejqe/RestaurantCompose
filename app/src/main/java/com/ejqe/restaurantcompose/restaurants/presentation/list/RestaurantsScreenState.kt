package com.ejqe.restaurantcompose.restaurants.presentation.list

import com.ejqe.restaurantcompose.restaurants.domain.Restaurant

data class RestaurantsScreenState(
    val restaurant: List<Restaurant>,
    val isLoading: Boolean,
    val error: String? = null
)
