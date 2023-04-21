package com.ejqe.restaurantcompose

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException

class RestaurantsViewModel(private val stateHandle: SavedStateHandle) : ViewModel() {
    private var restInterface: RestaurantsApiService
    private var restaurantsDao = RestaurantsDb.getDaoInstance(RestaurantsApplication.getAppContext())
    val state = mutableStateOf(emptyList<Restaurant>())
    private val errorHandler = CoroutineExceptionHandler{ _, exception -> exception.printStackTrace()}

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://restaurantcompose-6fdb4-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .build()
        restInterface = retrofit.create(RestaurantsApiService::class.java)
        getRestaurants()
    }

    companion object {
        const val FAVORITES = "favorites"
    }

    private fun getRestaurants() {
        viewModelScope.launch(errorHandler) {
            val restaurants = getAllRestaurants()
            state.value = restaurants.restoreSelections()
        }
    }

    private suspend fun getAllRestaurants(): List<Restaurant> {
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
            return@withContext restaurantsDao.getAll()
        }
    }

    private suspend fun refreshCache() {
        val remoteRestaurants = restInterface.getRestaurants()
        val favoriteRestaurants = restaurantsDao.getAllFavorited()
        restaurantsDao.addAll(remoteRestaurants)
        restaurantsDao.updateAll(
            favoriteRestaurants.map { PartialRestaurant(it.id, true) })

    }
    override fun onCleared() {
        super.onCleared()
    }

    private suspend fun toggleFavoriteRestaurant(id: Int, oldValue: Boolean) =
        withContext(Dispatchers.IO) {
            restaurantsDao.update(PartialRestaurant(id = id, isFavorite = !oldValue))
            restaurantsDao.getAll()
        }

    fun toggleFavorite(id: Int) {
        val restaurants = state.value.toMutableList()
        val itemIndex = restaurants.indexOfFirst { it.id == id }
        val item = restaurants[itemIndex]
        restaurants[itemIndex] =
            item.copy(isFavorite = !item.isFavorite)
        storeSelection(restaurants[itemIndex])

        viewModelScope.launch {
            val updatedRestaurants = toggleFavoriteRestaurant(id, item.isFavorite)
            state.value = updatedRestaurants
        }
    }

    private fun storeSelection(item: Restaurant) {
        val savedToggled = stateHandle
            .get<List<Int>?>(FAVORITES)
            .orEmpty().toMutableList()
        if (item.isFavorite)
            savedToggled.add(item.id)
        else
            savedToggled.remove(item.id)
        stateHandle[FAVORITES] = savedToggled

    }




    private fun List<Restaurant>.restoreSelections(): List<Restaurant> {
        stateHandle.get<List<Int>?>(FAVORITES)?.let { selectedIds ->
            val restaurantsMap = this.associateBy { it.id }.toMutableMap()
            selectedIds.forEach { id ->
                val restaurant = restaurantsMap[id] ?: return@forEach
                restaurantsMap[id] = restaurant.copy(isFavorite = true)
            }
            return restaurantsMap.values.toList()
        }
        return this
    }


}