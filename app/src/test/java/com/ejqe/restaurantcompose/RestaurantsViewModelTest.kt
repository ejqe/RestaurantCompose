package com.ejqe.restaurantcompose

import com.ejqe.restaurantcompose.restaurants.DummyContent
import com.ejqe.restaurantcompose.restaurants.data.RestaurantsRepository
import com.ejqe.restaurantcompose.restaurants.domain.GetInitialRestaurantsUseCase
import com.ejqe.restaurantcompose.restaurants.domain.GetSortedRestaurantsUseCase
import com.ejqe.restaurantcompose.restaurants.domain.ToggleRestaurantUseCase
import com.ejqe.restaurantcompose.restaurants.presentation.list.RestaurantsScreenState
import com.ejqe.restaurantcompose.restaurants.presentation.list.RestaurantsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class RestaurantsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    @Test
    fun initialState_isProduced() = scope.runTest {
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
        val restaurantsRepository = RestaurantsRepository(FakeApiService(), FakeRoomDao(), dispatcher)
        val getSortedRestaurantsUseCase = GetSortedRestaurantsUseCase(restaurantsRepository)
        val getInitialRestaurantsUseCase = GetInitialRestaurantsUseCase(restaurantsRepository, getSortedRestaurantsUseCase)
        val toggleRestaurantUseCase = ToggleRestaurantUseCase(restaurantsRepository, getSortedRestaurantsUseCase)
        return RestaurantsViewModel(getInitialRestaurantsUseCase, toggleRestaurantUseCase, dispatcher)
    }

    @Test
    fun stateWithContent_isProduced() = scope.runTest {
        val restaurants = DummyContent.getDomainRestaurants()
        val viewModel = getViewModel()
        advanceUntilIdle()
        val currentState = viewModel.state.value
        assert(
            currentState == RestaurantsScreenState(
                restaurant = restaurants,
                isLoading = false,
                error = null)
        )
    }

}