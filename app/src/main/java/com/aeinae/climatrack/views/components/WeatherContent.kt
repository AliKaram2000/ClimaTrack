package com.aeinae.climatrack.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.aeinae.climatrack.domain.models.CurrentWeather
import com.aeinae.climatrack.domain.models.DailyForecast
import com.aeinae.climatrack.domain.models.HourlyForecast
import com.aeinae.climatrack.ui.theme.ClimaTrackTheme
import com.aeinae.climatrack.utils.Constants
import com.aeinae.climatrack.utils.DateTimeUtils
import kotlin.math.roundToInt


// ── Unit suffix helpers ──

internal fun tempSuffix(tempUnit: String): String = when (tempUnit) {
    Constants.Units.METRIC   -> "°C"
    Constants.Units.IMPERIAL -> "°F"
    else                     -> "K"
}

internal fun windSuffix(windUnit: String): String = when (windUnit) {
    Constants.WindUnits.MILES_HOUR -> "mph"
    else                          -> "m/s"
}

internal const val PRESSURE_SUFFIX = "hPa"


// ── Cache banner ──

@Composable
internal fun CacheBanner(lastUpdated: Long) {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(extendedColors.accent.copy(alpha = 0.1f))
            .border(
                width = dimens.hairlineHeight,
                color = extendedColors.accent.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = dimens.spacing12, vertical = dimens.spacing8)
    ) {
        Text(
            text = "Offline · ${DateTimeUtils.lastUpdatedText(lastUpdated)}",
            style = MaterialTheme.typography.labelSmall,
            color = extendedColors.accent
        )
    }
}


// ── Hero banner ──

@Composable
internal fun MainBanner(current: CurrentWeather, tempUnit: String) {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(extendedColors.heroBackground)
            .padding(
                top = dimens.spacing48,
                bottom = dimens.spacing32,
                start = dimens.spacing24,
                end = dimens.spacing24
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${current.cityName}, ${current.country}",
                style = MaterialTheme.typography.headlineMedium,
                color = extendedColors.heroTextPrimary
            )
            Spacer(modifier = Modifier.height(dimens.spacing4))
            Text(
                text = DateTimeUtils.currentDateFormatted(),
                style = MaterialTheme.typography.bodyMedium,
                color = extendedColors.heroTextSecondary
            )

            Spacer(modifier = Modifier.height(dimens.spacing24))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                WeatherIcon(
                    iconCode = current.iconCode,
                    description = current.description,
                    size = 80
                )
                Spacer(modifier = Modifier.width(dimens.spacing8))
                Text(
                    text = "${current.temperature.roundToInt()}${tempUnit}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Light,
                        fontSize = 72.sp
                    ),
                    color = extendedColors.heroTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(dimens.spacing8))

            Text(
                text = current.description.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = extendedColors.heroTextPrimary
            )
            Spacer(modifier = Modifier.height(dimens.spacing4))
            Text(
                text = "Feels like ${current.feelsLike.roundToInt()}${tempUnit}",
                style = MaterialTheme.typography.bodySmall,
                color = extendedColors.heroTextSecondary
            )
        }
    }
}


// ── Weather details 2×2 grid ──

@Composable
internal fun WeatherDetailsSection(
    current: CurrentWeather,
    windUnit: String
) {
    val dimens = ClimaTrackTheme.dimens

    Column(verticalArrangement = Arrangement.spacedBy(dimens.spacing12)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimens.spacing12)
        ) {
            DetailCard(
                label = "Wind",
                value = "${current.windSpeed} $windUnit",
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "Humidity",
                value = "${current.humidity}%",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimens.spacing12)
        ) {
            DetailCard(
                label = "Pressure",
                value = "${current.pressure} $PRESSURE_SUFFIX",
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "Cloudiness",
                value = "${current.cloudiness}%",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
internal fun DetailCard(label: String, value: String, modifier: Modifier = Modifier) {
    val dimens = ClimaTrackTheme.dimens

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .border(
                width = dimens.hairlineHeight,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            )
            .padding(dimens.spacing12)
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(dimens.spacing4))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}


// ── Hourly forecast ──

@Composable
internal fun HourlyForecastRow(hourly: List<HourlyForecast>, tempUnit: String) {
    val dimens = ClimaTrackTheme.dimens

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(dimens.spacing8),
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        items(hourly, key = { it.dateTime }) { item ->
            HourlyCard(item = item, tempUnit = tempUnit)
        }
    }
}

@Composable
internal fun HourlyCard(item: HourlyForecast, tempUnit: String) {
    val dimens = ClimaTrackTheme.dimens

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clip(MaterialTheme.shapes.small)
            .border(
                width = dimens.hairlineHeight,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.small
            )
            .padding(vertical = dimens.spacing8, horizontal = dimens.spacing4)
    ) {
        Text(
            text = DateTimeUtils.formatHour(item.dateTime),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(dimens.spacing4))
        WeatherIcon(iconCode = item.iconCode, description = item.description, size = 36)
        Spacer(modifier = Modifier.height(dimens.spacing4))
        Text(
            text = "${item.temperature.roundToInt()}${tempUnit}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


// ── Daily forecast ──

@Composable
internal fun DailyForecastList(daily: List<DailyForecast>, tempUnit: String) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        daily.forEachIndexed { index, item ->
            DailyRow(item = item, tempUnit = tempUnit)
            if (index < daily.lastIndex) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
internal fun DailyRow(item: DailyForecast, tempUnit: String) {
    val dimens = ClimaTrackTheme.dimens

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimens.spacing8),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = DateTimeUtils.formatDayName(item.dateTime),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1.2f)
        ) {
            WeatherIcon(iconCode = item.iconCode, description = item.description, size = 32)
            Spacer(modifier = Modifier.width(dimens.spacing8))
            Text(
                text = item.description.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1
            )
        }

        Text(
            text = "${item.tempMin.roundToInt()}° / ${item.tempMax.roundToInt()}${tempUnit}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.8f)
        )
    }
}


// ── Shared small composables ──

@Composable
internal fun WeatherIcon(iconCode: String, description: String, size: Int) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(Constants.iconUrl(iconCode))
            .crossfade(true)
            .build()
    )
    Image(
        painter = painter,
        contentDescription = description,
        modifier = Modifier.size(size.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
internal fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        letterSpacing = 1.5.sp
    )
}

@Composable
internal fun SectionDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
        thickness = 0.5.dp
    )
}