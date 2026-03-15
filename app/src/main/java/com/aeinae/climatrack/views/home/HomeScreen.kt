package com.aeinae.climatrack.views.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.aeinae.climatrack.app.ClimaTrackApplication
import com.aeinae.climatrack.domain.models.CurrentWeather
import com.aeinae.climatrack.domain.models.DailyForecast
import com.aeinae.climatrack.domain.models.HourlyForecast
import com.aeinae.climatrack.ui.theme.ClimaTrackTheme
import com.aeinae.climatrack.utils.Constants
import com.aeinae.climatrack.utils.DateTimeUtils
import kotlin.math.roundToInt


private fun tempSuffix(tempUnit: String): String = when (tempUnit) {
    Constants.Units.METRIC   -> "°C"
    Constants.Units.IMPERIAL -> "°F"
    else                     -> "K"
}

private fun windSuffix(windUnit: String): String = when (windUnit) {
    Constants.WindUnits.MILES_HOUR -> "mph"
    else                          -> "m/s"
}

private const val PRESSURE_SUFFIX = "hPa"


private fun requestLocationPermissions(
    launcher: androidx.activity.result.ActivityResultLauncher<Array<String>>
) {
    launcher.launch(
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as ClimaTrackApplication
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(application))

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) viewModel.refresh()
    }

    LaunchedEffect(Unit) {
        if (viewModel.requiresGpsPermission() && !hasLocationPermission(context)) {
            requestLocationPermissions(permissionLauncher)
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingContent()

            is HomeUiState.Success -> SuccessContent(
                state = state,
                modifier = Modifier.fillMaxSize()
            )

            is HomeUiState.Error -> ErrorContent(
                message = state.message,
                onRetry = {
                    if (viewModel.requiresGpsPermission() && !hasLocationPermission(context)) {
                        requestLocationPermissions(permissionLauncher)
                    } else {
                        viewModel.refresh()
                    }
                }
            )
        }
    }
}


@Composable
private fun LoadingContent() {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = extendedColors.accent,
                strokeWidth = 2.dp,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(dimens.spacing12))
            Text(
                text = "Loading forecast…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}


@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(dimens.spacing16)
        ) {
            Text(
                text = "Unable to Load Weather",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(dimens.spacing8))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(dimens.spacing24))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = extendedColors.accent,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Retry", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}


@Composable
private fun SuccessContent(
    state: HomeUiState.Success,
    modifier: Modifier = Modifier
) {
    val dimens = ClimaTrackTheme.dimens
    val scrollState = rememberScrollState()
    val tempUnit = tempSuffix(state.tempUnit)
    val windUnit = windSuffix(state.windUnit)

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = dimens.spacing16)
            .padding(top = dimens.spacing24, bottom = dimens.spacing32)
    ) {
        if (state.isFromCache) {
            CacheBanner(lastUpdated = state.lastUpdated)
            Spacer(modifier = Modifier.height(dimens.spacing8))
        }

        MainBanner(current = state.current, tempUnit = tempUnit)

        Spacer(modifier = Modifier.height(dimens.spacing24))
        SectionDivider()
        Spacer(modifier = Modifier.height(dimens.spacing24))

        WeatherDetailsSection(
            current = state.current,
            windUnit = windUnit
        )

        Spacer(modifier = Modifier.height(dimens.spacing24))
        SectionDivider()
        Spacer(modifier = Modifier.height(dimens.spacing24))

        SectionHeader(title = "Hourly Forecast")
        Spacer(modifier = Modifier.height(dimens.spacing12))
        HourlyForecastRow(hourly = state.hourly, tempUnit = tempUnit)

        Spacer(modifier = Modifier.height(dimens.spacing24))
        SectionDivider()
        Spacer(modifier = Modifier.height(dimens.spacing24))

        SectionHeader(title = "5-Day Forecast")
        Spacer(modifier = Modifier.height(dimens.spacing12))
        DailyForecastList(daily = state.daily, tempUnit = tempUnit)
    }
}


@Composable
private fun CacheBanner(lastUpdated: Long) {
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


@Composable
private fun MainBanner(current: CurrentWeather, tempUnit: String) {
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

@Composable
private fun WeatherDetailsSection(
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
private fun DetailCard(label: String, value: String, modifier: Modifier = Modifier) {
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


@Composable
private fun HourlyForecastRow(hourly: List<HourlyForecast>, tempUnit: String) {
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
private fun HourlyCard(item: HourlyForecast, tempUnit: String) {
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


@Composable
private fun DailyForecastList(daily: List<DailyForecast>, tempUnit: String) {
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
private fun DailyRow(item: DailyForecast, tempUnit: String) {
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


@Composable
private fun WeatherIcon(iconCode: String, description: String, size: Int) {
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
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        letterSpacing = 1.5.sp
    )
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
        thickness = 0.5.dp
    )
}