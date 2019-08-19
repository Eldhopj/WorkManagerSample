package com.example.eldho.workmanagersample;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkerClass extends Worker {
    private Context mContext;
    public WorkerClass(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    /**Inside this func we do our work that we need to exec in background*/
    public Result doWork() {
        // Work here is to display a notification
        NotificationUtils.createNotifications(mContext,"Success","work done successfully");
        return Result.success(); // if work is success
//        return Result.failure(); // if work is failture
//        return Result.retry() // if we want to retry the task
    }
}
