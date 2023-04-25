package com.ejqe.restaurantcompose

import com.ejqe.restaurantcompose.restaurants.DummyContent
import com.ejqe.restaurantcompose.restaurants.data.remote.RemoteRestaurant
import com.ejqe.restaurantcompose.restaurants.data.remote.RestaurantsApiService
import kotlinx.coroutines.delay

class FakeApiService: RestaurantsApiService {
    override suspend fun getRestaurants(): List<RemoteRestaurant> {
        delay(1000)
        return DummyContent.getRemoteRestaurants()
    }

    override suspend fun getRestaurant(id: Int): Map<String, RemoteRestaurant> {
        TODO("Not yet implemented")
    }

}