package com.example.eldho.workmanagersample;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkerClass extends Worker {
    private Context mContext;
    private static final String TAG = "WorkerClass";
    public WorkerClass(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    /**Inside this func we do our work that we need to exec in background*/
    public Result doWork() {
        // Work here is to display a notification
        try {
            NotificationUtils.createNotifications(mContext, "Success", "work done successfully");
            return Result.success(); // if work is success
        } catch (Throwable throwable) {

            // Technically WorkManager will return Result.failure()
            // but it's best to be explicit about it.
            // Thus if there were errors, we're return FAILURE
            Log.e(TAG, "Error applying blur", throwable);
            return Result.failure();
//          return Result.retry(); // if we want to retry the task
        }
    }
}
