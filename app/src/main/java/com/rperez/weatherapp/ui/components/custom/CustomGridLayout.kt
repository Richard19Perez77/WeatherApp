package com.rperez.weatherapp.ui.components.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import com.rperez.weatherapp.colors.RandomColor.getRandomColors

@Composable
fun CustomGridLayout(modifier: Modifier, temp: String) {
    var colors = getRandomColors()
    Layout(
        content = {
            Box {
                Text(
                    modifier = Modifier
                        .background(colors[0]),
                    text = "icon"
                )
            }
            Box(
                modifier = Modifier
                    .background(colors[1])
            ) { }
            Box(
                modifier = Modifier
                    .background(colors[2])
            ) { }
            Box(
                modifier = Modifier
                    .background(colors[3])
            ) { }
            Box {
                Text(
                    modifier = Modifier
                        .background(colors[4]),
                    text = temp
                )
            }
            Box(
                modifier = Modifier
                    .background(colors[5])
            ) { }
            Box(
                modifier = Modifier
                    .background(colors[6])
            ) { }
            Box(
                modifier = Modifier
                    .background(colors[7])
            ) { }
            Box {
                Text(
                    modifier = Modifier
                        .background(colors[8]),
                    text = "city"
                )
            }
        },
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) { measurables, constraints ->
        val cellSize = constraints.maxWidth / 3
        val placeables = measurables.map { measurable ->
            measurable.measure(
                constraints.copy(
                    minWidth = cellSize,
                    maxWidth = cellSize,
                    minHeight = cellSize,
                    maxHeight = cellSize
                )
            )
        }

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val row = index / 3
                val column = index % 3
                placeable.placeRelative(x = column * cellSize, y = row * cellSize)
            }
        }
    }
}