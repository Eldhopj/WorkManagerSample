package com.example.eldho.workmanagersample.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.eldho.workmanagersample.MainActivity
import com.example.eldho.workmanagersample.notification.NotificationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WorkerClass(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    /**
     *  doWork() method is run async (Because of CoroutineWorker, else if we use Worker it will run synchronously ) on a background thread provided by WorkManager
     *  */
    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        val data = inputData // get the data which are passing to workRequest
        val desc = data.getString(MainActivity.TASK_DESC)
        return@withContext try {
            // Work here is to display a notification
            NotificationUtils.createNotifications(appContext, "Success", desc)

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
            //          return Result.retry(); // if we want to retry the task
        }
    }

    // Sending success data/Result back to activity/fragment which then observed in `getWorkInfoByIdLiveData`
    private fun getSendResult(isDone: Boolean, taskOutput: String): Data {
        return Data.Builder()
            //Note : We can send almost all data types
            .putString(TASK_OUTPUT, taskOutput)
            .putBoolean(IS_DONE, isDone)
            .build()
    }

    companion object {
        private const val TAG = "WorkerClass"
        const val TASK_OUTPUT = "taskOutPut"
        const val IS_DONE = "isDone"
    }
}