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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable function representing the HeartScreen.
 * This screen displays saved temperature entries along with their adverse health effects.
 * If there are no saved entries, it shows a message with rare facts.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param getAllTemperatures Function to retrieve all temperature entries as a Flow of a list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartScreen(
    modifier: Modifier,
    getAllTemperatures: () -> List<TemperatureEntity>
) {
    // Collect temperature entries and sort them by timestamp in descending order.
    var allTemps = remember { mutableStateOf<List<TemperatureEntity>>(emptyList())}

    // Get the string resource for rare facts.
    var rareFacts = stringResource(R.string.rare_facts)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO){
            allTemps.value = getAllTemperatures.invoke()
            allTemps.value.sortedBy { it -> it.timeStamp }.reversed()
        }
    }

    // Layout for the HeartScreen.
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Check if there are no temperature entries to display.
        if (allTemps.value.isEmpty()) {
            // Display health tips if there are no entries.
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

            // Display a list of temperature entries in a LazyColumn.
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(2f)
                    .padding(8.dp, 0.dp, 8.dp, 0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {

                // Create a TemperatureItem for each temperature entry.
                items(allTemps.value) { temperature ->
                    TemperatureItem(temperature)
                }
            }

            // Display a card with rare facts at the bottom of the screen.
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

/**
 * Composable function to display individual temperature details.
 *
 * @param temperature The temperature entity containing the details to display.
 */
@Composable
fun TemperatureItem(temperature: TemperatureEntity) {

    // Retrieve alerts based on the temperature entity.
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
            // Display city name and date in a row.
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = temperature.city,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Text(
                    text = temperature.date,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            // Display temperature, humidity, and pressure in another row.
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
            // Display additional description about the temperature.
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = temperature.desc,
                style = MaterialTheme.typography.bodyLarge
            )
            // Display alerts if any exist.
            if (alertList.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = alertList.joinToString("\n\n"),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Visible,
                )
            }
            // Format and display the timestamp of the temperature entry.
            val date = Date(temperature.timeStamp)
            val pattern = "EEEE, MMMM d, yyyy h:mm a"
            val format = SimpleDateFormat(pattern, Locale.getDefault())
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = format.format(date),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Function to get health alerts based on temperature and humidity levels.
 *
 * @param context The context to retrieve string resources.
 * @param temperature The temperature entity containing data to assess health risks.
 * @return A list of alerts corresponding to the health risks based on the temperature.
 */
fun getAlerts(context: Context, temperature: TemperatureEntity): List<String> {
    var temp = mutableListOf<String>()

    // Add alerts based on temperature thresholds.
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
    // Add alerts based on humidity thresholds.
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
    // Add alerts based on pressure thresholds.
    if (temperature.pressure < 980) {
        temp.add(context.getString(R.string.low_air_pressure_1))
    }
    if (temperature.pressure > 1050) {
        temp.add(context.getString(R.string.high_air_pressure_1))
    }

    return temp
}