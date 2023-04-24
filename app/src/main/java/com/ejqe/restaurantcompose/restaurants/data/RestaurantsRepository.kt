package com.ejqe.restaurantcompose.restaurants.data

import com.ejqe.restaurantcompose.RestaurantsApplication
import com.ejqe.restaurantcompose.restaurants.data.local.LocalRestaurant
import com.ejqe.restaurantcompose.restaurants.data.local.PartialLocalRestaurant
import com.ejqe.restaurantcompose.restaurants.data.local.RestaurantsDao
import com.ejqe.restaurantcompose.restaurants.data.local.RestaurantsDb
import com.ejqe.restaurantcompose.restaurants.data.remote.RestaurantsApiService
import com.ejqe.restaurantcompose.restaurants.domain.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class RestaurantsRepository @Inject constructor(
    private var restInterface: RestaurantsApiService,
    private var restaurantsDao: RestaurantsDao
    ) {

    suspend fun toggleFavoriteRestaurant(id: Int, value: Boolean) =
        withContext(Dispatchers.IO) {
            restaurantsDao.update(PartialLocalRestaurant(id = id, isFavorite = value))
        }

    suspend fun loadRestaurants() {
        return withContext(Dispatchers.IO) {
            try {
                refreshCache()
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException, is ConnectException, is HttpException -> {
                        if (restaurantsDao.getAll().isEmpty())
                            throw Exception(
                                "Something went wrong." + "We have no data."
                            )
                    }
                    else -> throw e
                }
            }
        }
    }

    suspend fun getRestaurants(): List<Restaurant> {
        return withContext(Dispatchers.IO) {
            return@withContext restaurantsDao.getAll().map {
                Restaurant(it.id, it.title, it.description, it.isFavorite)
            }
        }
    }

    private suspend fun refreshCache() {
        val remoteRestaurants = restInterface.getRestaurants()
        val favoriteRestaurants = restaurantsDao.getAllFavorited()
        restaurantsDao.addAll(remoteRestaurants.map {
            LocalRestaurant(it.id, it.title, it.description, isFavorite = false)
        })  //save to room
        restaurantsDao.updateAll(
            favoriteRestaurants.map { PartialLocalRestaurant(it.id, true) })

    }
}