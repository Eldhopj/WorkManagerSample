package com.example.eldho.workmanagersample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.eldho.workmanagersample.workers.UploadWorker
import com.example.eldho.workmanagersample.workers.WorkerClass
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

/**
 * NOTE : WorkManager is intended for tasks that are not required to run immediately , and required to run reliably even if the app exits or the device restart
 * Where to use which : https://1.bp.blogspot.com/-0o3ndTbxAHQ/W8UdDaO6iaI/AAAAAAAAF0w/o-p-Ghm0xNsdgBX9ohELYO3UMB2ZO_FMQCLcBGAs/s1600/image1.png
 * https://android-developers.googleblog.com/2018/10/modern-background-execution-in-android.html
 *
 * Worker : The work needed to be done is defined here
 * WorkRequest : it define work, like which worker class is going to be executed.
 * The WorkRequest is an abstract class so we will use the direct subclasses so we will use the direct subclasses,"OneTimeWorkRequest" or "PeriodicWorkRequest"
 * WorkManager : it enqueues and managers the work request
 * WorkInfo : it contains info about the work like work id , status etc....
 */

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val mWorkManager: WorkManager by lazy { WorkManager.getInstance(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationWorkInfoListener()
        uploadingInfoListener()
        periodicNotificationWorkInfoListener()

    }

    fun oneTimeWork(view: View) {
        mWorkManager.enqueue(onetimeWorkReq())
    }

    fun chainWork(view: View) {
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

    //NOTE : minimum period is 15 mins between works
    fun periodicWork(view: View) {
        mWorkManager.enqueue(periodicWorkRequest())
    }


    // ---------- creation of works --------------//
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

    private fun periodicWorkRequest(): PeriodicWorkRequest {
        return PeriodicWorkRequest
            .Builder(WorkerClass::class.java,
                16, TimeUnit.MINUTES) //NOTE : minimum period is 15 mins between works
            .build()
    }
    // ---------- end creation of works --------------//


    /** Sending data to the worker class */
    private val dataToSend: Data
        get() = Data.Builder()
            .putString(TASK_DESC, "Sample Task") //Note : We can send almost all data types
            .putBoolean(IS_TO_BE_DONE, true)
            .build()

    /** creates constraints for job to run , the job will run when the constraints satisfies*/
    private val constraint: Constraints
        get() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.METERED)
            .setRequiresCharging(true)
            .build()


    // -----------Observe the work requests changes ---------------//
    private fun notificationWorkInfoListener() {
        mWorkManager.getWorkInfoByIdLiveData(onetimeWorkReq().id) // Filter by work id
            .observe(this, Observer { workInfo -> // If there are no matching work info, do nothing
                if (workInfo == null) { // WorkInfo : it contains info about the work like work id , status etc....
                    return@Observer
                }

                /* workInfo.state.name returns the state of the work
                * There are of 4 states BLOCKED -> ENQUEUED -> RUNNING -> SUCCEEDED*/
                workStatusTv.text = "Notification Status Update: " + workInfo.state.name

                if (workInfo.state.isFinished) {
                    Log.d(TAG, "work: " + "succeed")
                    Toast.makeText(this@MainActivity,
                        "result: " + workInfo.outputData.getString(WorkerClass.TASK_OUTPUT),
                        Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "result: " + workInfo.outputData.getString(WorkerClass.TASK_OUTPUT))
                }
            })
    }//Note : We can send almost all data types

    private fun uploadingInfoListener() {
        mWorkManager.getWorkInfoByIdLiveData(uploadingWorkReq().id)
            .observe(this, Observer { workInfo -> // If there are no matching work info, do nothing
                if (workInfo == null) { // WorkInfo : it contains info about the work like work id , status etc....
                    return@Observer
                }

                /* workInfo.state.name returns the state of the work
                * There are of 4 states BLOCKED -> ENQUEUED -> RUNNING -> SUCCEEDED*/
                workStatusTv.text = "Uploading Status Update: " + workInfo.state.name

                if (workInfo.state.isFinished) {
                    Log.d(TAG, "work: " + "succeed")
                    Toast.makeText(this@MainActivity,
                        "result: " + workInfo.outputData.getString(UploadWorker.TASK_OUTPUT),
                        Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "result: " + workInfo.outputData.getString(UploadWorker.TASK_OUTPUT))
                }
            })
    }//Note : We can send almost all data types

    private fun periodicNotificationWorkInfoListener() {
        mWorkManager.getWorkInfoByIdLiveData(periodicWorkRequest().id) // Filter by work id
            .observe(this, Observer { workInfo -> // If there are no matching work info, do nothing
                if (workInfo == null) { // WorkInfo : it contains info about the work like work id , status etc....
                    return@Observer
                }

                /* workInfo.state.name returns the state of the work
                * There are of 4 states BLOCKED -> ENQUEUED -> RUNNING -> SUCCEEDED*/
                workStatusTv.text = "Notification Status Update: " + workInfo.state.name

                if (workInfo.state.isFinished) {
                    Log.d(TAG, "work: " + "succeed")
                    Toast.makeText(this@MainActivity,
                        "result: " + workInfo.outputData.getString(WorkerClass.TASK_OUTPUT),
                        Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "result: " + workInfo.outputData.getString(WorkerClass.TASK_OUTPUT))
                }
            })
    }//Note : We can send almost all data types

    // -------------- end Observe the work requests changes -------------//


    companion object {
        const val TASK_DESC = "task_desc"
        const val IS_TO_BE_DONE = " isToBeDone"
    }
}