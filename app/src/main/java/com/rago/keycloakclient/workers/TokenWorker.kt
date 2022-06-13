package com.rago.keycloakclient.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rago.keycloakclient.repositories.auth.AuthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

@HiltWorker
class TokenWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val authRepository: AuthRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        Log.i(TAG, "doWork: Init")
        if (authRepository.getTokenRefresh() == null) return Result.success()
        return try {
            val token = authRepository.workerRefresh().first()
            Log.i(TAG, "doWork: ${token.accessToken}")
            Result.success()
        } catch (cancellationException: CancellationException) {
            Log.i(TAG, "doWork: Failure")
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "TokenWorker"
    }
}