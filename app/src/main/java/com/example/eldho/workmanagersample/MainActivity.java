package com.example.eldho.workmanagersample;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * NOTE : WorkManager is intended for tasks that are not required to run immediately , and required to run reliably even if the app exits or the device restart
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        workStatusListener();

    }

    private OneTimeWorkRequest onetimeWorkReq() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest
                .Builder(WorkerClass.class)
                .build();
        return workRequest;
    }

    // Observe the work changes
    private void workStatusListener () {
        WorkManager.getInstance(getApplicationContext())
                .getWorkInfoByIdLiveData(onetimeWorkReq().getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        // workInfo : live data where the status of work contains
                        workStatus.setText("Status : " + workInfo.getState().name());
                    }
                });
    }

    @OnClick(R.id.oneTimeWork)
    public void onClick() {
        // we need work manager to perform the work
        WorkManager.getInstance(getApplicationContext()).enqueue(onetimeWorkReq());
    }


}
