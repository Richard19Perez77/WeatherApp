package com.rperez.weatherapp.ui.util.colors

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

object RandomColor {
    private val random = Random
    fun getRandomColor(): Color {
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return Color(red, green, blue)
    }

    fun getRandomColors(count: Int = 9): List<Color> {
        return List(count) { getRandomColor() }
    }
}