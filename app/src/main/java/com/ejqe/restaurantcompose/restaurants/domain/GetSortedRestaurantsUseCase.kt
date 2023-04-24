package com.ejqe.restaurantcompose.restaurants.domain

import com.ejqe.restaurantcompose.restaurants.data.RestaurantsRepository

class GetSortedRestaurantsUseCase {
    private val repository: RestaurantsRepository = RestaurantsRepository()
    suspend operator fun invoke(): List<Restaurant> {
        return repository.getRestaurants().sortedBy { it.title }
    }
}