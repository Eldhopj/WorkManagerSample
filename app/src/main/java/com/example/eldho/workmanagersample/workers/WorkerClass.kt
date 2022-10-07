package com.example.eldho.workmanagersample.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.eldho.workmanagersample.MainActivity
import com.example.eldho.workmanagersample.notification.NotificationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@HiltWorker // Dependency injection in worker class is not a straight forward thing we needed to use hilt work for injecting dependencies into worker class
class WorkerClass @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationUtils: NotificationUtils
) :
    CoroutineWorker(appContext, workerParams) {

    /**
     * doWork() method is run async (Because of CoroutineWorker, else if we use Worker it will run synchronously ) on a background thread provided by WorkManager
     *  */
    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        val data = inputData // get the data which are passing to workRequest
        val desc = data.getString(MainActivity.TASK_DESC)
        return@withContext try {
            // Work here is to display a notification
            notificationUtils.createNotifications("Success", desc)

            val result = getSendResult(
                true,
                "Success") // Sending success data/Result back to activity/fragment
            Result.success(result) // if work is success
        } catch (throwable: Throwable) {

            // Technically WorkManager will return Result.failure()
            // but it's best to be explicit about it.
            // Thus if there were errors, we're return FAILURE
            Log.e(TAG, "Error ", throwable)
            val result = getSendResult(false, "Failure")
            Result.failure(result)
            //Result.retry()  // if we want to retry the task
        }
    }

    // Sending success data/Result back to activity/fragment which then observed in `getWorkInfoByIdLiveData`
    private fun getSendResult(isDone: Boolean, taskOutput: String): Data {
        return workDataOf(
            TASK_OUTPUT to taskOutput,
            IS_DONE to isDone
        )
    }

    companion object {
        private const val TAG = "WorkerClass"
        const val TASK_OUTPUT = "taskOutPut"
        const val IS_DONE = "isDone"
    }
}
