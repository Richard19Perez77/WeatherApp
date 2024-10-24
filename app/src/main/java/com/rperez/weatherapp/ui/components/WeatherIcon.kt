package com.rperez.weatherapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.rperez.weatherapp.R

/**
 * Composable function to display a weather icon based on a provided URL.
 *
 * This function uses Coil to asynchronously load an image from the given URL
 * and displays it within an Image component. It includes accessibility
 * semantics for better screen reader support.
 *
 * @param iconUrl The URL of the weather icon to be displayed. If null,
 *                the image will not be loaded.
 */
@Composable
fun WeatherIcon(iconUrl: String?) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .build()
    )

    // Retrieve the semantic description for accessibility
    var semanticString = stringResource(R.string.weather_icon_semantic)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier.Companion
            .semantics {
                contentDescription = semanticString
            }
            .size(128.dp)
            .testTag("icon_image"),
        contentScale = ContentScale.Companion.FillBounds
    )
}