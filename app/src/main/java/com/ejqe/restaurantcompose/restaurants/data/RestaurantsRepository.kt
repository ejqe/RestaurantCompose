package com.ejqe.restaurantcompose.restaurants.data

import com.ejqe.restaurantcompose.RestaurantsApplication
import com.ejqe.restaurantcompose.restaurants.data.local.LocalRestaurant
import com.ejqe.restaurantcompose.restaurants.data.local.PartialLocalRestaurant
import com.ejqe.restaurantcompose.restaurants.data.local.RestaurantsDao
import com.ejqe.restaurantcompose.restaurants.data.local.RestaurantsDb
import com.ejqe.restaurantcompose.restaurants.data.remote.RestaurantsApiService
import com.ejqe.restaurantcompose.restaurants.di.IoDispatcher
import com.ejqe.restaurantcompose.restaurants.domain.Restaurant
import kotlinx.coroutines.CoroutineDispatcher
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
    private var restaurantsDao: RestaurantsDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
    ) {

    suspend fun toggleFavoriteRestaurant(id: Int, value: Boolean) =
        withContext(dispatcher) {
            restaurantsDao.update(PartialLocalRestaurant(id = id, isFavorite = value))
        }

    //Get from Remote
    suspend fun loadRestaurants() {
        return withContext(dispatcher) {
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

    //Get from Local
    suspend fun getRestaurants(): List<Restaurant> {
        return withContext(dispatcher) {
            return@withContext restaurantsDao.getAll().map {
                Restaurant(it.id, it.title, it.description, it.isFavorite)
            }
        }
    }

    //Convert Remote to Local
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