package com.aeinae.climatrack.views.settings

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Loaded(
        val locationMode: String,
        val tempUnit: String,
        val windUnit: String,
        val language: String
    ) : SettingsUiState
}