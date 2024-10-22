package com.rperez.weatherapp.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rperez.weatherapp.R
import com.rperez.weatherapp.data.local.db.TemperatureEntity

/**
 * Screen to show the entries saved with more detail, won't focus on telemetrics of weather but the weather's adverse health effects.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartScreen(
    modifier: Modifier,
    getAllTemperatures: suspend () -> List<TemperatureEntity>
) {
    var allTemps = remember { mutableStateOf(emptyList<TemperatureEntity>()) }
    var loading = remember { mutableStateOf(true) }
    var error = remember { mutableStateOf<String?>(null) }

    var context = LocalContext.current
    var rareFacts = context.getString(R.string.rare_facts)

    LaunchedEffect(Unit) {
        try {
            loading.value = true
            allTemps.value = getAllTemperatures().sortedBy { item -> item.date }.reversed()
            loading.value = false
        } catch (_: Exception) {
            error.value = context.getString(R.string.error_loading_temperatures)
            loading.value = false
        }
    }

    if (loading.value) {
        CircularProgressIndicator()
    } else if (error.value != null) {
        Text(text = error.value.toString())
    } else {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(2f)
                    .padding(8.dp, 0.dp, 8.dp, 0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(allTemps.value) { temperature ->
                    TemperatureItem(temperature)
                }
            }
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    text = rareFacts
                )
            }
        }
    }
}

@Composable
fun TemperatureItem(temperature: TemperatureEntity) {
    var alertList = getAlerts(LocalContext.current, temperature)
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = temperature.city,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = temperature.date,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "${temperature.temperature} °C",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "${temperature.humidity}% Humidity",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "${temperature.pressure} hPa",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = temperature.desc,
                style = MaterialTheme.typography.titleMedium
            )
            if (alertList.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = alertList.joinToString("\n\n"),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Visible,
                )
            }
        }
    }
}

fun getAlerts(context: Context, temperature: TemperatureEntity): List<String> {
    var temp = mutableListOf<String>()
    if (temperature.temperature < 10.0) {
        temp.add(context.getString(R.string.hypthermia_1))
        temp.add(context.getString(R.string.respiratory_1))
    }
    if (temperature.temperature < 0) {
        temp.add(context.getString(R.string.frostbite_1))
        temp.add(context.getString(R.string.heartattack_1))
    }
    if (temperature.temperature > 27.0) {
        temp.add(context.getString(R.string.heat_exhaust_1))
        temp.add(context.getString(R.string.dehydration_1))
    }
    if (temperature.temperature > 40.0) {
        temp.add(context.getString(R.string.heat_stroke_1))
    }
    if (temperature.temperature > 21.0) {
        temp.add(context.getString(R.string.skin_issues_1))
    }
    if (temperature.temperature > 15.0) {
        temp.add(context.getString(R.string.allergies_1))
    }
    if (temperature.humidity < 30) {
        temp.add(context.getString(R.string.humidity_1))
    }
    if (temperature.humidity > 50) {
        temp.add(context.getString(R.string.humidity_2))
    }
    if (temperature.humidity > 60) {
        temp.add(context.getString(R.string.humidity_3))
    }
    if (temperature.humidity > 70) {
        temp.add(context.getString(R.string.humidity_4))
    }
    if (temperature.pressure < 980) {
        temp.add(context.getString(R.string.low_air_pressure_1))
    }
    if (temperature.pressure > 1050) {
        temp.add(context.getString(R.string.high_air_pressure_1))
    }

    return temp
}