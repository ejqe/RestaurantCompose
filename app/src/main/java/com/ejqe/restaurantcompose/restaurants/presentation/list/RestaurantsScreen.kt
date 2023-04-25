package com.ejqe.restaurantcompose.restaurants.presentation.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ejqe.restaurantcompose.restaurants.domain.Restaurant
import com.ejqe.restaurantcompose.restaurants.presentation.Description
import com.ejqe.restaurantcompose.ui.theme.RestaurantComposeTheme


@Composable
fun RestaurantsScreen(
    state: RestaurantsScreenState,
    onItemClick: (id: Int) -> Unit,
    onFavoriteClick: (id: Int, oldValue: Boolean) -> Unit
) {

    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    )   {
        LazyColumn(
            contentPadding = PaddingValues(
                vertical = 8.dp,


                horizontal = 8.dp
            )
        ) {
            items(state.restaurant) { restaurant ->
                RestaurantItem(
                    restaurant,
                    onItemClick = { id -> onItemClick(id) },
                    onFavoriteClick = { id, oldValue -> onFavoriteClick(id, oldValue) }
                )
            }
        }
        if(state.isLoading)
            CircularProgressIndicator(
                Modifier.semantics { this.contentDescription = Description.RESTAURANTS_LOADING })
        if(state.error !=null)
            Text(state.error)
    }
}

@Composable
fun RestaurantItem(item: Restaurant,
                   onFavoriteClick: (id: Int, oldValue: Boolean) -> Unit,
                   onItemClick: (id: Int) -> Unit) {

    val icon =  if (item.isFavorite) Icons.Filled.Favorite
                else Icons.Filled.FavoriteBorder
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onItemClick(item.id) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
        ) {
            RestaurantIcon(
                Icons.Filled.Place,
                Modifier.weight(0.15f)
            )
            RestaurantDetails(item.title, item.description, Modifier.weight(0.85f), Alignment.Start)
            RestaurantIcon(icon,Modifier.weight(0.15f))
                { onFavoriteClick(item.id, item.isFavorite) }
        }
    }
}


@Composable
fun RestaurantIcon(icon: ImageVector, modifier: Modifier, onClick: () -> Unit = {}) {
    Image(
        imageVector = icon,
        contentDescription = "Restaurant icon",
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() }
    )
}

@Composable
fun RestaurantDetails(title: String, description: String, modifier: Modifier,
                      horizontalAlignment: Alignment.Horizontal) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = description,
                style = MaterialTheme.typography.body2
            )

        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RestaurantComposeTheme {
        RestaurantsScreen(
            RestaurantsScreenState(restaurant = listOf(), isLoading = true),
                {},
                { _, _ ->}
        )


    }
}


