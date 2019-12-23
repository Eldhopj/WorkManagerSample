package com.example.eldho.workmanagersample;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * We can pass multiple work request to work manager, if we pass several work req to a single method call, work manager do it in parallel
 */

//Worker : The work needed to be done is defined here
public class WorkerClass extends Worker {
    private Context mContext;
    private static final String TAG = "WorkerClass";
    public static final String TASK_DESC = "task_desc";
    public static final String IS_TO_BE_DONE = " isToBeDone";
    public static final String TASK_OUTPUT = "taskOutPut";
    public static final String IS_DONE = "isDone";

    public WorkerClass(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    /**Inside this func we do our work that we need to exec in background*/
    public Result doWork() {
        Data data = getInputData(); // get the data which are passing to workRequest
        String desc = data.getString(TASK_DESC);
        try {
            // Work here is to display a notification
            NotificationUtils.createNotifications(mContext, "Success", desc);
            Data result = getResultForSending(true, "Success"); // Sending success data/Result back to activity/fragment
            return Result.success(result); // if work is success
        } catch (Throwable throwable) {

            // Technically WorkManager will return Result.failure()
            // but it's best to be explicit about it.
            // Thus if there were errors, we're return FAILURE
            Log.e(TAG, "Error ", throwable);
            Data result = getResultForSending(false, "Failure");
            return Result.failure(result);
//          return Result.retry(); // if we want to retry the task
        }
    }

    // Sending success data/Result back to activity/fragment which then observed in `getWorkInfoByIdLiveData`
    private Data getResultForSending(boolean isDone, String taskOutput) {
        Data data = new Data.Builder()
                .putString(WorkerClass.TASK_OUTPUT, taskOutput) //Note : We can send almost all data types
                .putBoolean(WorkerClass.IS_DONE, isDone)
                .build();
        return data;
    }
}
