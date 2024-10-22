package com.rperez.weatherapp.ui.screen

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
 * Simple icon for URL
 */
@Composable
fun WeatherIcon(iconUrl: String?) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .build()
    )

    var semanticString = stringResource(R.string.weather_icon_semantic)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .semantics {
                contentDescription = semanticString
            }
            .size(128.dp)
            .testTag("icon_image"),
        contentScale = ContentScale.FillBounds
    )
}