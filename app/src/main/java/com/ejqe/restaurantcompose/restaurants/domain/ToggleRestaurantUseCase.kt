package com.ejqe.restaurantcompose.restaurants.domain

import com.ejqe.restaurantcompose.restaurants.data.RestaurantsRepository
import com.ejqe.restaurantcompose.restaurants.domain.GetSortedRestaurantsUseCase
import javax.inject.Inject

class ToggleRestaurantUseCase @Inject constructor(
    private val repository: RestaurantsRepository,
    private val getSortedRestaurantsUseCase: GetSortedRestaurantsUseCase
    ){
    suspend operator fun invoke(id: Int, oldValue: Boolean): List<Restaurant> {
        val newFav = oldValue.not()
        repository.toggleFavoriteRestaurant(id, newFav)
        return getSortedRestaurantsUseCase()
    }
}