package com.ejqe.restaurantcompose

data class RestaurantsScreenState(
    val restaurant: List<Restaurant>,
    val isLoading: Boolean,
    val error: String? = null
)
