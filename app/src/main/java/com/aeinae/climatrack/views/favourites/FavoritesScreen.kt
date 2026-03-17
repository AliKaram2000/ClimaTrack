package com.aeinae.climatrack.views.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
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
import com.aeinae.climatrack.data.local.database.entity.FavoriteEntity
import com.aeinae.climatrack.ui.theme.ClimaTrackTheme



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onFavoriteClick: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as ClimaTrackApplication
    val viewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModel.factory(application))

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = extendedColors.accent,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add favourite"
                )
            }
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            // Screen title
            Text(
                text = "Favourites",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(
                    start = dimens.spacing16,
                    end = dimens.spacing16,
                    top = dimens.spacing24,
                    bottom = dimens.spacing16
                )
            )

            when (val state = uiState) {
                is FavoritesUiState.Loading -> {
                    LoadingContent(modifier = Modifier.weight(1f))
                }
                is FavoritesUiState.Success -> {
                    if (state.favorites.isEmpty()) {
                        EmptyContent(modifier = Modifier.weight(1f))
                    } else {
                        FavoritesList(
                            favorites = state.favorites,
                            onFavoriteClick = onFavoriteClick,
                            onDelete = { viewModel.deleteFavorite(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = ClimaTrackTheme.extendedColors.accent,
            strokeWidth = 2.dp,
            modifier = Modifier.size(40.dp)
        )
    }
}


@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No favourites yet.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(ClimaTrackTheme.dimens.spacing4))
            Text(
                text = "Tap + to add a location.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesList(
    favorites: List<FavoriteEntity>,
    onFavoriteClick: (Int) -> Unit,
    onDelete: (FavoriteEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimens = ClimaTrackTheme.dimens

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = dimens.spacing16,
            end = dimens.spacing16,
            bottom = dimens.spacing48
        ),
        verticalArrangement = Arrangement.spacedBy(dimens.spacing12)
    ) {
        items(
            items = favorites,
            key = { it.id }
        ) { favorite ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        onDelete(favorite)
                        true
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = false,
                backgroundContent = { DismissBackground() },
                modifier = Modifier.animateItem()
            ) {
                FavoriteCard(
                    favorite = favorite,
                    onClick = { onFavoriteClick(favorite.id) }
                )
            }
        }
    }
}


@Composable
private fun DismissBackground() {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.small)
            .background(extendedColors.destructive)
            .padding(horizontal = dimens.spacing16),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = extendedColors.destructiveForeground
        )
    }
}


@Composable
private fun FavoriteCard(
    favorite: FavoriteEntity,
    onClick: () -> Unit
) {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(extendedColors.cardBackground)
            .border(
                width = dimens.hairlineHeight,
                color = extendedColors.cardStroke,
                shape = MaterialTheme.shapes.small
            )
            .clickable(onClick = onClick)
            .padding(dimens.spacing16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = favorite.cityName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(dimens.spacing2))
            Text(
                text = favorite.country,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}