package com.ejqe.restaurantcompose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ejqe.restaurantcompose.restaurants.DummyContent
import com.ejqe.restaurantcompose.restaurants.domain.Restaurant
import com.ejqe.restaurantcompose.restaurants.presentation.Description
import com.ejqe.restaurantcompose.restaurants.presentation.list.RestaurantsScreen
import com.ejqe.restaurantcompose.restaurants.presentation.list.RestaurantsScreenState
import com.ejqe.restaurantcompose.ui.theme.RestaurantComposeTheme
import org.junit.Rule
import org.junit.Test

class RestaurantsScreenTest {
    @get:Rule
    val testRule: ComposeContentTestRule = createComposeRule()

    @Test
    fun initialState_isRendered() {
        testRule.setContent {
            RestaurantComposeTheme {
                RestaurantsScreen(
                    state = RestaurantsScreenState(
                        restaurant = emptyList(), isLoading = true) ,
                    onItemClick = { },
                    onFavoriteClick = { _: Int, _: Boolean -> }
                )
            }
        }
        testRule.onNodeWithContentDescription(Description.RESTAURANTS_LOADING
            ).assertIsDisplayed()

    }

    @Test
    fun stateWithContent_isRendered() {
        val restaurants = DummyContent.getDomainRestaurants()
        testRule.setContent {
            RestaurantComposeTheme {
                RestaurantsScreen(
                    state = RestaurantsScreenState(
                        restaurant = restaurants, isLoading = false) ,
                    onItemClick = { },
                    onFavoriteClick = { _: Int, _: Boolean -> }
                )
            }
        }
        testRule.onNodeWithText(restaurants[0].title).assertIsDisplayed()
        testRule.onNodeWithText(restaurants[0].description).assertIsDisplayed()

        testRule.onNodeWithContentDescription(Description.RESTAURANTS_LOADING
            ).assertDoesNotExist()
    }

    @Test
    fun stateWithContent_ClickOnItem_isRegistered() {
        val restaurants = DummyContent.getDomainRestaurants()
        val targetRestaurant = restaurants[0]
        testRule.setContent {
            RestaurantComposeTheme {
                RestaurantsScreen(
                    state = RestaurantsScreenState(
                        restaurant = restaurants, isLoading = false
                    ),
                    onItemClick = { id ->
                        assert(id == targetRestaurant.id)
                    },
                    onFavoriteClick = { _: Int, _: Boolean -> }
                )
            }
        }
        testRule.onNodeWithText(targetRestaurant.title).performClick()
    }

}