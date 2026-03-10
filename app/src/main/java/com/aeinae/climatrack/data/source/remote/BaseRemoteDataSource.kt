package com.aeinae.climatrack.data.source.remote

import kotlin.coroutines.cancellation.CancellationException

abstract class BaseRemoteDataSource {
    protected suspend fun <T> safeApiCall(
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}