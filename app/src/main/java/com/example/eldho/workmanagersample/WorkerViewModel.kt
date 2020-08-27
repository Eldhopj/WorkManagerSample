package com.example.eldho.workmanagersample;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class WorkerViewModel extends AndroidViewModel {

    private WorkManager mWorkManager;
    private LiveData<WorkInfo> workInfoLiveData;

    public WorkerViewModel(@NonNull Application application) {
        super(application);
        mWorkManager = WorkManager.getInstance(application); //WorkManager : it enqueues and managers the work request
        workInfoLiveData = mWorkManager.getWorkInfoByIdLiveData(onetimeWorkReq().getId());
    }

    /**
     * WorkRequest : it define work, like which worker class is going to be executed.
     * The WorkRequest is an abstract class so we will use the direct subclasses so we will use the direct subclasses,"OneTimeWorkRequest" or "PeriodicWorkRequest"
     */
    private OneTimeWorkRequest onetimeWorkReq() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest
                .Builder(WorkerClass.class)
                .setInputData(getDataToSend())
                .setConstraints(getConstraint())
                .build();
        return workRequest;
    }

    /**
     * Minimum period length is 15 minutes (same as JobScheduler)
     * Worker classes cannot be chained in a PeriodicWorkRequest
     */
    private PeriodicWorkRequest periodicWorkReq() {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest
                .Builder(WorkerClass.class, 20, TimeUnit.MINUTES) // Repeats work in every 20 mins
                .setInputData(getDataToSend())
                .setConstraints(getConstraint())
                .build();
        return workRequest;
    }


    // Sending data to the worker class
    private Data getDataToSend() {
        Data data = new Data.Builder()
                .putString(WorkerClass.TASK_DESC, "Sample Task") //Note : We can send almost all data types
                .putBoolean(WorkerClass.IS_TO_BE_DONE, true)
                .build();
        return data;
    }

    // creates constraints for job to run , the job will run when the constraints satisfies
    private Constraints getConstraint() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(false)
                .build();
        return constraints;
    }

    void startOneTimeWorkRequest() {
        // we need work manager to perform the work
        mWorkManager.enqueue(onetimeWorkReq());
    }

    void startPeriodicWorkRequest() {
        // we need work manager to perform the work
        mWorkManager.enqueue(periodicWorkReq());
    }

    LiveData<WorkInfo> getOutputWorkInfo() {
        return workInfoLiveData;
    }
}
