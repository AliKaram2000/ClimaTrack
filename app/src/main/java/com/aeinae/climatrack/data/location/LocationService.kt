package com.aeinae.climatrack.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class LocationService(private val context: Context) {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Result<Pair<Double, Double>> {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            return Result.failure(SecurityException("Location permission not granted"))
        }

        return try {
            val location = getLastLocation()

            if (location != null) {
                Result.success(Pair(location.latitude, location.longitude))
            } else {
                Result.failure(Exception("Unable to get location"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastLocation(): Location? {
        return client.lastLocation.await()
    }
}