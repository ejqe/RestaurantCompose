package com.ejqe.restaurantcompose.restaurants.data.local

import androidx.room.*
import com.ejqe.restaurantcompose.restaurants.data.local.LocalRestaurant
import com.ejqe.restaurantcompose.restaurants.data.local.PartialLocalRestaurant

@Dao
interface RestaurantsDao {
    @Query("SELECT * FROM restaurants")
    suspend fun getAll(): List<LocalRestaurant>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(restaurant: List<LocalRestaurant>)  //Save to Room

    @Update(entity = LocalRestaurant::class)
    suspend fun update(partialLocalRestaurant: PartialLocalRestaurant)

    @Update(entity = LocalRestaurant::class)
    suspend fun updateAll(partialLocalRestaurant: List<PartialLocalRestaurant>)

    @Query("SELECT * FROM restaurants WHERE is_Favorite = 1")
    suspend fun getAllFavorited(): List<LocalRestaurant>
}