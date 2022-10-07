package com.example.eldho.workmanagersample

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class WorkManagerApp : Application(), Configuration.Provider {

    /**
     * Ref: https://developer.android.com/training/dependency-injection/hilt-jetpack#workmanager
     *
     * Manifest changes
     *Ref: https://developer.android.com/topic/libraries/architecture/workmanager/advanced/custom-configuration
     * */
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
