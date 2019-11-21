package com.example.eldho.workmanagersample;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class WorkerViewModel extends AndroidViewModel {

    private WorkManager mWorkManager;
    private LiveData<WorkInfo> workInfoLiveData;

    public WorkerViewModel(@NonNull Application application) {
        super(application);
        mWorkManager = WorkManager.getInstance(application);
        workInfoLiveData = mWorkManager.getWorkInfoByIdLiveData(onetimeWorkReq().getId());
    }

    private OneTimeWorkRequest onetimeWorkReq() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest
                .Builder(WorkerClass.class)
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
                .setRequiredNetworkType(NetworkType.METERED)
                .setRequiresCharging(true)
                .build();
        return constraints;
    }

    void startOneTimeWorkRequest() {
        // we need work manager to perform the work
        mWorkManager.enqueue(onetimeWorkReq());
    }

    LiveData<WorkInfo> getOutputWorkInfo() {
        return workInfoLiveData;
    }
}
