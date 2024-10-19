package com.rperez.weatherapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

    LaunchedEffect(Unit) {
        try {
            loading.value = true
            allTemps.value = getAllTemperatures()
            loading.value = false
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

@Composable
fun TemperatureItem(temperature: TemperatureEntity) {
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
            Text(text = "Date: ${temperature.date}", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Temperature: ${temperature.temperature}°C",
                style = MaterialTheme.typography.bodyLarge
            )

            val locationText = if (temperature.local) "Local Data" else "Remote Data"
            Text(text = locationText, style = MaterialTheme.typography.bodySmall)
        }
    }
}