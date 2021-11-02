package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.work.*
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Override application to setup background work via WorkManager
 */

class AsteroidRadarApplication: Application() {

    // instantiate database as singleton
    // Advanced way is to use Dependency Injection Libraries like Dagger or KOIN
    private val database by lazy { AsteroidsDatabase.getDatabase(this) }

    // instantiate repository to be re-used across the app
    val repository by lazy { AsteroidsRepository(database.asteroidDao) }

    val applicationScope = CoroutineScope(Dispatchers.Default)
    // initialization function that does not block the main thread
    // should be called from inside onCreate without using
    // a background thread to avoid issues caused when initialization happens after WorkManager is used
    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    /**
     * a repeatingRequest variable that uses a PeriodicWorkRequestBuilder
     * to create a PeriodicWorkRequest for your RefreshDataWorker.
     * It should run once every day.
     */
    private fun setupRecurringWork() {
        // Constraints to prevent work from occurring
        // when there is no network access or the device is low on battery.
        // WIFI, charging ...
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest
                = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        // Get an instance of WorkManager and call enqueueUniquePeriodicWork to schedule the work.
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }


    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    override fun onCreate() {
        super.onCreate()
        delayedInit()
        Log.e("Application class : ","in onCreate()")
    }
}