package com.ejqe.restaurantcompose

import com.ejqe.restaurantcompose.restaurants.data.RestaurantsRepository
import com.ejqe.restaurantcompose.restaurants.domain.GetInitialRestaurantsUseCase
import com.ejqe.restaurantcompose.restaurants.domain.GetSortedRestaurantsUseCase
import com.ejqe.restaurantcompose.restaurants.domain.ToggleRestaurantUseCase
import com.ejqe.restaurantcompose.restaurants.presentation.list.RestaurantsScreenState
import com.ejqe.restaurantcompose.restaurants.presentation.list.RestaurantsViewModel
import org.junit.Test

class RestaurantsViewModelTest {
    @Test
    fun initialState_isProduced() {
        val viewModel = getViewModel()
        val initialState = viewModel.state.value
        assert(
            initialState == RestaurantsScreenState(
                restaurant = emptyList(),
                isLoading = true,
                error = null)
        )
    }



    private fun getViewModel(): RestaurantsViewModel {
        val restaurantsRepository = RestaurantsRepository(FakeApiService(), FakeRoomDao())
        val getSortedRestaurantsUseCase = GetSortedRestaurantsUseCase(restaurantsRepository)
        val getInitialRestaurantsUseCase = GetInitialRestaurantsUseCase(restaurantsRepository, getSortedRestaurantsUseCase)
        val toggleRestaurantUseCase = ToggleRestaurantUseCase(restaurantsRepository, getSortedRestaurantsUseCase)
        return RestaurantsViewModel(getInitialRestaurantsUseCase, toggleRestaurantUseCase)
    }

}