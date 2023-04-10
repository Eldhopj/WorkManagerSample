package com.example.eldho.workmanagersample.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.eldho.workmanagersample.MainActivity.Companion.IS_TO_BE_DONE
import com.example.eldho.workmanagersample.MainActivity.Companion.TASK_DESC
import com.example.eldho.workmanagersample.workers.UploadWorker
import com.example.eldho.workmanagersample.workers.WorkerClass
import java.util.concurrent.TimeUnit

class WorkerViewModel(application: Application) : AndroidViewModel(application) {

    private val mWorkManager: WorkManager by lazy { WorkManager.getInstance(application) }

    val oneTimeWorkObserver: LiveData<WorkInfo> by lazy { mWorkManager.getWorkInfoByIdLiveData(oneTimeWorkReq.id) }
    val uploadingWorkObserver: LiveData<WorkInfo> by lazy { mWorkManager.getWorkInfoByIdLiveData(uploadingWorkReq.id) }
    val periodicWorkObserver: LiveData<WorkInfo> by lazy { mWorkManager.getWorkInfoByIdLiveData(periodicWorkReq.id) }

    /**
     * WorkRequest : it define work, like which worker class is going to be executed.
     * The WorkRequest is an abstract class so we will use the direct subclasses so we will use the direct subclasses,"OneTimeWorkRequest" or "PeriodicWorkRequest"
     */
    //---------- creation of works --------------//

    private val oneTimeWorkReq: OneTimeWorkRequest by lazy {
        OneTimeWorkRequest.Builder(WorkerClass::class.java)
            .setInputData(dataToSend)
            .setConstraints(constraint)
//            .setInitialDelay(15, TimeUnit.DAYS)
            .build()
    }

    private val uploadingWorkReq: OneTimeWorkRequest by lazy {
        OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setInputData(dataToSend)
            .setConstraints(constraint)
            .build()
    }

    //NOTE : we cant chain periodic work request
    private val periodicWorkReq: PeriodicWorkRequest by lazy {
        PeriodicWorkRequest
            .Builder(WorkerClass::class.java,
                16, TimeUnit.MINUTES) //NOTE : minimum period is 15 mins between works
            .build()
    }
    //---------- end of creation of works --------------//


    /** Sending data to the worker class */
    private val dataToSend
        get() = workDataOf(
            TASK_DESC to "Sample Task",
            IS_TO_BE_DONE to true
        )

    /** creates constraints for job to run , the job will run when the constraints satisfies*/
    private val constraint: Constraints
        get() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.METERED)
            .setRequiresCharging(true)
            .build()

    fun startOneTimeWorkRequest() {
        mWorkManager.enqueue(oneTimeWorkReq)
    }

    fun startPeriodicWorkRequest() {
        mWorkManager.enqueue(periodicWorkReq)
    }

    fun startParallelWork() {
        val parallelWorks = listOf(oneTimeWorkReq, uploadingWorkReq)
        mWorkManager.enqueue(parallelWorks)
    }

    fun startChainingWorkRequest() {

        /**
         * we can execute works parallel as well as sequential works together in a chain
         * */
        mWorkManager.beginWith(uploadingWorkReq)
            .then(oneTimeWorkReq)
            .enqueue()
    }
}
