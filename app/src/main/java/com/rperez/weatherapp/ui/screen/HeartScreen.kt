package com.rperez.weatherapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
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
                    .padding(8.dp, 0.dp, 8.dp, 0.dp),
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
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = temperature.date,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${temperature.temperature} °C",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = temperature.desc,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${temperature.humidity}% Humidity",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Air Pressure: ${temperature.pressure} hPa",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
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

fun getAlerts(temperature: TemperatureEntity): List<String> {
    var temp = mutableListOf<String>()
    if (temperature.temperature < 10.0) {
        temp.add("Hypothermia: Typically occurs when the body temperature drops below 95°F (35°C). Risk increases in temperatures below 50°F (10°C) with wind chill.")
        temp.add("Respiratory Issues: Cold air can affect individuals with asthma or COPD, often when temperatures drop below 50°F (10°C).")
    }
    if (temperature.temperature < 0) {
        temp.add("Frostbite: Can occur in temperatures below 32°F (0°C), especially with wind chill. Risk increases significantly in subzero temperatures.")
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
    if (temperature.humidity < 30) {
        temp.add("Humidity < 30%: Leads to respiratory irritation, dry skin, increased infection risk, static electricity discomfort.")
    }
    if (temperature.humidity > 50) {
        temp.add("Humidity > 50%: Risk of mold and dust mite growth increases, increased sweat and discomfort begins.")
    }
    if (temperature.humidity > 60) {
        temp.add("Humidity > 60%: Significant increase in mold, mildew, and dust mites; respiratory conditions may worsen.")
    }
    if (temperature.humidity > 70) {
        temp.add("Humidity > 70%: Heat-related illnesses, severe discomfort, and heightened allergy or asthma problems and possible joint pains.")
    }
    if (temperature.pressure < 980) {
        temp.add("Low Air Pressure (less than 980) threshold can vary by city but is associated with migraines, joint pain, respiratory issues (shortness of breath and asthma), sinus discomfort, altitude sickness and mood disturbances")
    }
    if (temperature.pressure > 1050) {
        temp.add("High air pressure (more than 1050) threshold can cause health concerns including, barometric pressure headaches, sinus pressure and congestion, joint pain relief, and blood pressure and circulatory concerns.")
    }

    return temp
}