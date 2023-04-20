package com.ejqe.restaurantcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ejqe.restaurantcompose.ui.theme.RestaurantComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RestaurantComposeTheme {
//                RestaurantsScreen()
                RestaurantDetailsScreen()
            }
        }
    }
}




