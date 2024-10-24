package com.rperez.weatherapp.ui.util.colors

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Object for generating random colors.
 * This utility provides functions to create random colors, which can be used in UI elements.
 */
object RandomColor {

    // Instance of Random to generate random integers
    private val random = Random

    /**
     * Generates a random color with RGB values.
     *
     * @return A Color object with randomly generated red, green, and blue components.
     */
    fun getRandomColor(): Color {
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return Color(red, green, blue)
    }

    /**
     * Generates a list of random colors.
     *
     * @param count The number of random colors to generate. Defaults to 9, from a grid of 9 items (tic tac toe layouts).
     * @return A list of Color objects, each generated randomly.
     */
    fun getRandomColors(count: Int = 9): List<Color> {
        return List(count) { getRandomColor() }
    }
}