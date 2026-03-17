package com.aeinae.climatrack.views.favourites.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aeinae.climatrack.app.ClimaTrackApplication
import com.aeinae.climatrack.data.remote.dto.geocoding.GeocodingResponseDto
import com.aeinae.climatrack.ui.theme.ClimaTrackTheme
import com.aeinae.climatrack.utils.Constants
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.animation.MapAnimationOptions


@Composable
fun AddFavoriteScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val application = context.applicationContext as ClimaTrackApplication
    val viewModel: AddFavoriteViewModel = viewModel(factory = AddFavoriteViewModel.factory(application))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(
                Point.fromLngLat(
                    Constants.DefaultSettings.LONGITUDE,
                    Constants.DefaultSettings.LATITUDE
                )
            )
            zoom(5.0)
        }
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    LaunchedEffect(state.selectedLat, state.selectedLon) {
        val lat = state.selectedLat ?: return@LaunchedEffect
        val lon = state.selectedLon ?: return@LaunchedEffect
        mapViewportState.flyTo(
            cameraOptions = cameraOptions {
                center(Point.fromLngLat(lon, lat))
                zoom(10.0)
            },
            animationOptions = MapAnimationOptions.mapAnimationOptions { duration(1000) }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(onBack = onBack)

        SearchSection(
            query = state.searchQuery,
            results = state.searchResults,
            isSearching = state.isSearching,
            onQueryChanged = viewModel::onSearchQueryChanged,
            onResultSelected = viewModel::onSearchResultSelected
        )

        MapboxMap(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            mapViewportState = mapViewportState,
            scaleBar = {},
            logo = {},
            attribution = {},
            onMapClickListener = { point ->
                viewModel.onMapTapped(point.latitude(), point.longitude())
                true
            }
        ) {
            state.selectedLat?.let { lat ->
                state.selectedLon?.let { lon ->
                    CircleAnnotation(
                        point = Point.fromLngLat(lon, lat)
                    ) {
                        circleRadius = 12.0
                        circleColor = Color(0xFFC47B3B)
                        circleStrokeWidth = 2.5
                        circleStrokeColor = Color(0xFF1B2A4A)
                        circleOpacity = 0.9
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = state.selectedLat != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            BottomPanel(
                state = state,
                onConfirm = viewModel::onConfirm
            )
        }
    }
}


@Composable
private fun TopBar(onBack: () -> Unit) {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(extendedColors.heroBackground)
            .padding(
                start = dimens.spacing4,
                end = dimens.spacing16,
                top = dimens.spacing8,
                bottom = dimens.spacing8
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = extendedColors.heroTextPrimary
            )
        }
        Text(
            text = "Add Favourite",
            style = MaterialTheme.typography.titleLarge,
            color = extendedColors.heroTextPrimary
        )
    }
}


@Composable
private fun SearchSection(
    query: String,
    results: List<GeocodingResponseDto>,
    isSearching: Boolean,
    onQueryChanged: (String) -> Unit,
    onResultSelected: (GeocodingResponseDto) -> Unit
) {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = dimens.spacing16, vertical = dimens.spacing12)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search city…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extendedColors.textHint
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = extendedColors.textMuted,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                when {
                    isSearching -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = extendedColors.accent,
                            strokeWidth = 2.dp
                        )
                    }
                    query.isNotEmpty() -> {
                        IconButton(onClick = { onQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = extendedColors.textMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = extendedColors.inputBackground,
                unfocusedContainerColor = extendedColors.inputBackground,
                focusedBorderColor = extendedColors.inputBorderFocused,
                unfocusedBorderColor = extendedColors.inputBorder,
                cursorColor = extendedColors.accent
            )
        )

        if (results.isNotEmpty()) {
            Spacer(modifier = Modifier.height(dimens.spacing8))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(extendedColors.cardBackground)
                    .border(
                        width = dimens.hairlineHeight,
                        color = extendedColors.cardStroke,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                results.forEachIndexed { index, dto ->
                    SearchResultRow(
                        dto = dto,
                        onClick = { onResultSelected(dto) }
                    )
                    if (index < results.lastIndex) {
                        HorizontalDivider(
                            color = extendedColors.divider,
                            thickness = dimens.hairlineHeight
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun SearchResultRow(
    dto: GeocodingResponseDto,
    onClick: () -> Unit
) {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    val displayText = buildString {
        append(dto.name ?: "Unknown")
        dto.state?.let { append(", $it") }
        dto.country?.let { append(", $it") }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = dimens.spacing12, vertical = dimens.spacing12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = extendedColors.textMuted,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(dimens.spacing12))
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyMedium,
            color = extendedColors.textPrimary
        )
    }
}


@Composable
private fun BottomPanel(
    state: AddFavoriteUiState,
    onConfirm: () -> Unit
) {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(extendedColors.cardBackground)
            .border(
                width = dimens.hairlineHeight,
                color = extendedColors.cardStroke
            )
            .padding(dimens.spacing16)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (state.isReverseGeocoding) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = extendedColors.accent,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(dimens.spacing8))
                Text(
                    text = "Finding location…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else if (state.selectedCityName != null) {
                Text(
                    text = buildString {
                        append(state.selectedCityName)
                        state.selectedCountry?.let { append(", $it") }
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        state.selectedLat?.let { lat ->
            state.selectedLon?.let { lon ->
                Spacer(modifier = Modifier.height(dimens.spacing4))
                Text(
                    text = "%.4f, %.4f".format(lat, lon),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(dimens.spacing16))

        val canConfirm = state.selectedLat != null
                && state.selectedCityName != null
                && !state.isReverseGeocoding

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = canConfirm,
            colors = ButtonDefaults.buttonColors(
                containerColor = extendedColors.accent,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = extendedColors.accent.copy(alpha = 0.4f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Add to Favourites",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}