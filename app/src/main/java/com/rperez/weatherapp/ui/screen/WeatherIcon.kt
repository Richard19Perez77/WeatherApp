package com.rperez.weatherapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun WeatherIcon(iconUrl: String) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier.size(128.dp),
        contentScale = ContentScale.FillBounds
    )
}