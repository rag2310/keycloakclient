package com.rago.keycloakclient

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.rago.keycloakclient.workers.TokenWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class KeyCloakClientApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .setMinimumLoggingLevel(Log.DEBUG)
        .build()

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setUpRecurringWork()
        }
    }

    private fun setUpRecurringWork() {
        val repeatingRequest =
            PeriodicWorkRequestBuilder<TokenWorker>(15L, TimeUnit.MINUTES).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "TokenWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}