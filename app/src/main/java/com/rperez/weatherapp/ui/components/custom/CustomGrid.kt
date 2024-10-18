package com.rperez.weatherapp.ui.components.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rperez.weatherapp.colors.RandomColor.getRandomColors

@Composable
fun CustomGrid(modifier: Modifier, temp: String) {
    val gridItems = (1..9).map { it.toString() }
    var colors = getRandomColors()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(gridItems) { item ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxSize()
                ) {
                    when (item) {
                        "1" -> Text(
                            text = "City",
                            modifier = Modifier
                                .background(colors[0])
                                .align(Alignment.TopStart)
                        )

                        "5" -> Text(
                            text = temp,
                            modifier = Modifier
                                .background(colors[1])
                                .align(Alignment.Center)
                        )

                        "9" -> Text(
                            text = "Icon",
                            modifier = Modifier
                                .background(colors[2])
                                .align(Alignment.BottomEnd)
                        )

                        else -> {}
                    }
                }
            }
        }
    }
}
