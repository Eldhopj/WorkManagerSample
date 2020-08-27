package com.example.eldho.workmanagersample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.example.eldho.workmanagersample.workers.UploadWorker
import java.util.concurrent.TimeUnit

class WorkerViewModel(application: Application) : AndroidViewModel(application) {

    private val mWorkManager: WorkManager by lazy { WorkManager.getInstance(application) }

    val oneTimeWorkReq: LiveData<WorkInfo> by lazy { mWorkManager.getWorkInfoByIdLiveData(onetimeWorkReq().id) }
    val uploadingWorkReq: LiveData<WorkInfo> by lazy { mWorkManager.getWorkInfoByIdLiveData(uploadingWorkReq().id) }
    val periodicWorkRequest: LiveData<WorkInfo> by lazy { mWorkManager.getWorkInfoByIdLiveData(periodicWorkRequest().id) }

    /**
     * WorkRequest : it define work, like which worker class is going to be executed.
     * The WorkRequest is an abstract class so we will use the direct subclasses so we will use the direct subclasses,"OneTimeWorkRequest" or "PeriodicWorkRequest"
     */
    //---------- creation of works --------------//
    private fun onetimeWorkReq(): OneTimeWorkRequest {
        return OneTimeWorkRequest.Builder(WorkerClass::class.java)
            .setInputData(dataToSend)
            .setConstraints(constraint)
            .build()
    }

    private fun uploadingWorkReq(): OneTimeWorkRequest {
        return OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setInputData(dataToSend)
            .setConstraints(constraint)
            .build()
    }

    //NOTE : we cant chain periodic work request
    private fun periodicWorkRequest(): PeriodicWorkRequest {
        return PeriodicWorkRequest
            .Builder(WorkerClass::class.java,
                16, TimeUnit.MINUTES) //NOTE : minimum period is 15 mins between works
            .build()
    }
    //---------- end of creation of works --------------//


    /** Sending data to the worker class */
    private val dataToSend: Data
        get() = Data.Builder()
            .putString(MainActivity.TASK_DESC, "Sample Task") //Note : We can send almost all data types
            .putBoolean(MainActivity.IS_TO_BE_DONE, true)
            .build()

    /** creates constraints for job to run , the job will run when the constraints satisfies*/
    private val constraint: Constraints
        get() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.METERED)
            .setRequiresCharging(true)
            .build()

    fun startOneTimeWorkRequest() {
        mWorkManager.enqueue(onetimeWorkReq())
    }

    fun startPeriodicWorkRequest() {
        mWorkManager.enqueue(periodicWorkRequest())
    }

    fun startChainingWorkRequest() {
        val parallelWorks = mutableListOf<OneTimeWorkRequest>()
        parallelWorks.add(onetimeWorkReq())
        parallelWorks.add(uploadingWorkReq())

        /**First execute the  onetimeWorkReq()
         * after that work, 2 works in execute in parallel,
         * after that the last sequential work will execute */
        mWorkManager.beginWith(uploadingWorkReq()) // sequential work
            .then(parallelWorks) // parallel works
            .then(onetimeWorkReq()) // sequential work
            .enqueue()
    }
}