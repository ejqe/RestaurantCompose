package com.ejqe.restaurantcompose


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class RestaurantsViewModel : ViewModel() {

    private val repository = RestaurantsRepository()
    private val _state = mutableStateOf(RestaurantsScreenState(restaurant = listOf(), isLoading = true))
    val state: State<RestaurantsScreenState> get() = _state //backing property
    private val errorHandler = CoroutineExceptionHandler{
            _, exception -> exception.printStackTrace()
            _state.value = _state.value.copy(error = exception.message, isLoading = false)}

    init { getRestaurants() }


    private fun getRestaurants() {
        viewModelScope.launch(errorHandler) {
            val restaurants = repository.getAllRestaurants()
            _state.value = _state.value.copy(restaurant = restaurants, isLoading = false)
        }
    }
    fun toggleFavorite(id: Int, oldValue: Boolean) {
        viewModelScope.launch(errorHandler) {
            val updatedRestaurants = repository.toggleFavoriteRestaurant(id, oldValue)
            _state.value = _state.value.copy(restaurant = updatedRestaurants)
        }
    }


    override fun onCleared() {
        super.onCleared()
    }







}