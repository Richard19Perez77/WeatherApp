package com.rperez.weatherapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rperez.weatherapp.data.local.db.TemperatureEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartScreen(
    getAllTemperatures: suspend () -> List<TemperatureEntity>
) {
    var allTemps = remember { mutableStateOf(emptyList<TemperatureEntity>()) }
    var loading = remember { mutableStateOf(true) }
    var error = remember { mutableStateOf<String?>(null) }
    var localWeatherList = mutableListOf<TemperatureEntity>()

    LaunchedEffect(Unit) {
        try {
            loading.value = true
            allTemps.value = getAllTemperatures()
            loading.value = false
            localWeatherList.addAll(allTemps.value.filter { it.local })
        } catch (_: Exception) {
            error.value = "Error loading temperatures"
            loading.value = false
        }
    }

    if (loading.value) {
        CircularProgressIndicator()
    } else if (error.value != null) {
        Text(text = error.value.toString())
    } else {
        Column {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = ""
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(allTemps.value) { temperature ->
                    TemperatureItem(temperature)
                }
            }
        }
    }
}

@Composable
fun TemperatureItem(temperature: TemperatureEntity) {
    var alertList = getAlerts(temperature)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Date: ${temperature.date} Temperature: ${temperature.temperature}°C",
                style = MaterialTheme.typography.titleMedium
            )
            var locationText =
                if (temperature.local) "Local Data for ${temperature.city}: ${temperature.desc}" else "Remote Data for ${temperature.city}: ${temperature.desc}"
            Text(text = locationText, style = MaterialTheme.typography.bodyMedium)
            if (alertList.isNotEmpty())
                Text(
                    modifier = Modifier.background(Color.Red),
                    text = alertList.joinToString(" "),
                    style = MaterialTheme.typography.bodySmall,
                )
        }
    }
}

fun getAlerts(temperature: TemperatureEntity): List<String> {
    var temp = mutableListOf<String>()
    if (temperature.temperature in 0.0..10.0) {
        temp.add("Hypothermia: Typically occurs when the body temperature drops below 95°F (35°C). Risk increases in temperatures below 50°F (10°C) with wind chill.")
    }
    if (temperature.temperature < 10.0) {
        temp.add("Respiratory Issues: Cold air can affect individuals with asthma or COPD, often when temperatures drop below 50°F (10°C).")
    }
    if (temperature.temperature < 0) {
        temp.add("Frostbite: Can occur in temperatures below 32°F (0°C), especially with wind chill. Risk increases significantly in subzero temperatures.")
    }
    if (temperature.temperature < 0) {
        temp.add("Increased Heart Attack Risk: Increased risk can occur in temperatures below 32°F (0°C), especially for those with existing heart conditions.")
    }
    if (temperature.temperature > 27.0) {
        temp.add("Heat Exhaustion: Usually occurs in temperatures above 80°F (27°C), especially with high humidity and prolonged physical activity.")
        temp.add("Dehydration: Risk increases in temperatures above 80°F (27°C), especially during exercise or outdoor activities.")
    }
    if (temperature.temperature > 40.0) {
        temp.add("Heat Stroke: Can occur at temperatures above 104°F (40°C), particularly when coupled with exertion and dehydration.")
    }
    if (temperature.temperature > 21.0) {
        temp.add("Skin Issues (Sunburn): Can happen when UV index levels are high, which can occur at various temperatures, often when it’s sunny and above 70°F (21°C).")
    }
    if (temperature.temperature > 15.0) {
        temp.add("Allergies: Pollen counts can rise with temperatures as low as 60°F (15°C), depending on the plant species.")
    }
    return temp
}