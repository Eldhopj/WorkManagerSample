package com.example.eldho.workmanagersample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * NOTE : WorkManager is intended for tasks that are not required to run immediately , and required to run reliably even if the app exits or the device restart
 * Where to use which : https://1.bp.blogspot.com/-0o3ndTbxAHQ/W8UdDaO6iaI/AAAAAAAAF0w/o-p-Ghm0xNsdgBX9ohELYO3UMB2ZO_FMQCLcBGAs/s1600/image1.png
 *                      https://android-developers.googleblog.com/2018/10/modern-background-execution-in-android.html
 *
 * Worker : The work needed to be done is defined here
 * WorkRequest : it define work, like which worker class is going to be executed.
 * The WorkRequest is an abstract class so we will use the direct subclasses so we will use the direct subclasses,"OneTimeWorkRequest" or "PeriodicWorkRequest"
 * WorkManager : it enqueues and managers the work request
 * WorkInfo : it contains info about the work like work id , status etc....
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.oneTimeWork)
    Button oneTimeWork;
    @BindView(R.id.workStatus)
    TextView workStatus;
    private static final String TAG = "MainActivity";
    private WorkManager mWorkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mWorkManager = WorkManager.getInstance(getApplicationContext());
        workInfoListener();

    }

    private OneTimeWorkRequest onetimeWorkReq() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest
                .Builder(WorkerClass.class)
                .setConstraints(getConstraint())
                .build();
        return workRequest;
    }

    // creates constraints for job to run , the job will run when the constraints satisfies
    private Constraints getConstraint() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.METERED)
                .setRequiresCharging(true)
                .build();
        return constraints;
    }

    // Observe the work changes
    private void workInfoListener() {
        mWorkManager.getWorkInfoByIdLiveData(onetimeWorkReq().getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        // If there are no matching work info, do nothing
                        if (workInfo == null) {
                            return;
                        }
                        // workInfo : live data where the status of work contains
                        workStatus.setText("Status : " + workInfo.getState().name());
                        if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            Log.d(TAG, "onChanged: " + "succeed");
                        }
                    }
                });
    }

    @OnClick(R.id.oneTimeWork)
    public void onClick() {
        // we need work manager to perform the work
        mWorkManager.enqueue(onetimeWorkReq());
    }


}
