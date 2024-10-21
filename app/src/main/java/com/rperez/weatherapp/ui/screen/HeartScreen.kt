package com.rperez.weatherapp.ui.screen

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rperez.weatherapp.data.local.db.TemperatureEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartScreen(
    modifier : Modifier,
    getAllTemperatures: suspend () -> List<TemperatureEntity>
) {
    var allTemps = remember { mutableStateOf(emptyList<TemperatureEntity>()) }
    var loading = remember { mutableStateOf(true) }
    var error = remember { mutableStateOf<String?>(null) }
    var localWeatherList = mutableListOf<TemperatureEntity>()

    LaunchedEffect(Unit) {
        try {
            loading.value = true
            allTemps.value = getAllTemperatures().sortedBy { item -> item.date }.reversed()
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
                horizontalArrangement = Arrangement.End
            ) {
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
                    text = temperature.city,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
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
        temp.add("Low Air Pressure (less than 980 hPa) threshold can vary by city but is associated with migraines, joint pain, respiratory issues (shortness of breath and asthma), sinus discomfort, altitude sickness and mood disturbances")
    }
    if (temperature.pressure > 1050) {
        temp.add("High air pressure (more than 1050 hPa) threshold can cause health concerns including, barometric pressure headaches, sinus pressure and congestion, joint pain relief, and blood pressure and circulatory concerns.")
    }

    return temp
}

var rareFacts = """
Cold Weather Can Boost Brain Function

Exposure to cold weather can improve cognitive function, as the body works harder to maintain core temperature, leading to increased alertness and focus. Studies suggest that people tend to think more clearly in cooler environments compared to very hot conditions.

Rain and Arthritis Pain

There's a common belief that rainy weather worsens joint pain, but science hasn’t conclusively proven a direct link. However, changes in barometric pressure (which often occurs before rain) may cause joints to expand slightly, which could trigger pain in people with arthritis.

Sunlight and Immune System

Moderate sun exposure boosts vitamin D production, which is essential for immune function. Vitamin D helps the body fight off pathogens and may reduce the risk of autoimmune diseases. However, too much UV exposure can suppress the immune system, so balance is key.

Thunderstorms Can Trigger Asthma

Thunderstorms can cause a spike in asthma attacks due to a phenomenon called "thunderstorm asthma." During a storm, pollen grains and mold spores are broken into smaller fragments by rain and wind, making them easier to inhale and triggering asthma in sensitive individuals.

Humidity and Dehydration

In humid weather, the body's ability to cool itself by sweating is reduced. Because sweat doesn't evaporate as efficiently in high humidity, the body continues to produce more sweat, increasing the risk of dehydration without you realizing it.

Dry Air and Skin

Cold, dry weather causes moisture loss from the skin, leading to dry, flaky skin and exacerbating conditions like eczema. It's not just winter, though—air conditioning in the summer can also dry out your skin, so moisturizing year-round is important.

Barometric Pressure and Migraines

Changes in barometric pressure, especially a rapid drop, can trigger migraines. People who are sensitive to pressure changes may experience headaches even before a storm or drastic weather shift happens.

Hot Weather and Mood Swings

Higher temperatures can increase aggression and irritability in some individuals. Studies have shown a correlation between extremely hot weather and spikes in crime rates, possibly due to discomfort, dehydration, or the body's struggle to regulate heat.

Wind and Mental Health

Some studies suggest that strong, persistent winds (like those in windy regions) can negatively affect mood, leading to feelings of anxiety or depression. In particular, the dry, warm wind known as the Foehn wind has been linked to mood disturbances.

Weather and Heart Attacks

Cold weather puts extra strain on the cardiovascular system, increasing the risk of heart attacks. When it's cold, blood vessels constrict, and blood pressure rises, making it harder for the heart to pump blood efficiently. Sudden exertion in cold weather can increase heart attack risk, especially in people with pre-existing conditions.

These are just a few examples of how weather can impact personal health in ways people might not expect!
"""