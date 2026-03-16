package com.aeinae.climatrack.views.favourites.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aeinae.climatrack.app.ClimaTrackApplication
import com.aeinae.climatrack.ui.theme.ClimaTrackTheme
import com.aeinae.climatrack.views.components.CacheBanner
import com.aeinae.climatrack.views.components.DailyForecastList
import com.aeinae.climatrack.views.components.HourlyForecastRow
import com.aeinae.climatrack.views.components.MainBanner
import com.aeinae.climatrack.views.components.SectionDivider
import com.aeinae.climatrack.views.components.SectionHeader
import com.aeinae.climatrack.views.components.WeatherDetailsSection
import com.aeinae.climatrack.views.components.tempSuffix
import com.aeinae.climatrack.views.components.windSuffix


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDetailScreen(
    favoriteId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as ClimaTrackApplication
    val viewModel: FavoriteDetailViewModel = viewModel(
        factory = FavoriteDetailViewModel.factory(favoriteId, application)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Fixed back button — always visible regardless of state
        BackRow(onBack = onBack)

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            when (val state = uiState) {
                is FavoriteDetailUiState.Loading -> LoadingContent()

                is FavoriteDetailUiState.Success -> SuccessContent(
                    state = state,
                    modifier = Modifier.fillMaxSize()
                )

                is FavoriteDetailUiState.Error -> ErrorContent(
                    message = state.message,
                    onRetry = { viewModel.refresh() }
                )
            }
        }
    }
}


@Composable
private fun BackRow(onBack: () -> Unit) {
    val dimens = ClimaTrackTheme.dimens

    IconButton(
        onClick = onBack,
        modifier = Modifier.padding(
            start = dimens.spacing4,
            top = dimens.spacing8
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurface
        )
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
    state: FavoriteDetailUiState.Success,
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
            .padding(bottom = dimens.spacing32)
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