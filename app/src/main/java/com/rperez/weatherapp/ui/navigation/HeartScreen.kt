package com.rperez.weatherapp.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rperez.weatherapp.R
import com.rperez.weatherapp.data.local.db.TemperatureEntity
import kotlinx.coroutines.flow.Flow

/**
 * Screen to show the entries saved with more detail, won't focus on telemetrics of weather but the weather's adverse health effects.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartScreen(
    modifier: Modifier,
    getAllTemperatures: () -> Flow<List<TemperatureEntity>>
) {
    val allTemps1 = getAllTemperatures().collectAsStateWithLifecycle(emptyList())

    var rareFacts = stringResource(R.string.rare_facts)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (allTemps1.value.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(2f)
                    .padding(8.dp, 0.dp, 8.dp, 0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(allTemps1.value) { temperature ->
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
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = temperature.date,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "${temperature.temperature} °C",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${temperature.humidity}% Humidity",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${temperature.pressure} hPa",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = temperature.desc,
                style = MaterialTheme.typography.bodyLarge
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