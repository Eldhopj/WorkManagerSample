package com.example.eldho.workmanagersample.workers

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.eldho.workmanagersample.MainActivity

private const val TAG = "UploadWorker"

class UploadWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val data = inputData // get the data which are passing to workRequest
        val length = data.getLong(MainActivity.TASK_DESC, 4)
        return try {
            Thread.sleep(length * 1000)

            val result = getSendResult(true, "Success") // Sending success data/Result back to activity/fragment
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
        const val TASK_OUTPUT = "taskOutPut"
        const val IS_DONE = "isDone"
    }
}