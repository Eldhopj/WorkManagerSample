package com.example.eldho.workmanagersample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.WorkInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * NOTE : WorkManager is intended for tasks that are not required to run immediately , and required to run reliably even if the app exits or the device restart
 * Where to use which : https://1.bp.blogspot.com/-0o3ndTbxAHQ/W8UdDaO6iaI/AAAAAAAAF0w/o-p-Ghm0xNsdgBX9ohELYO3UMB2ZO_FMQCLcBGAs/s1600/image1.png
 * https://android-developers.googleblog.com/2018/10/modern-background-execution-in-android.html
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.oneTimeWork)
    Button oneTimeWork;
    @BindView(R.id.workStatus)
    TextView workStatus;
    @BindView(R.id.periodicTimeWork)
    Button periodicTimeWork;
    private WorkerViewModel viewModel;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this).get(WorkerViewModel.class);
        workInfoListener();
    }


    // Observe the work changes

    /**
     * WorkInfo : it contains info about the work like work id , status etc....
     */
    private void workInfoListener() {
        viewModel.getOutputWorkInfo().observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                // If there are no matching work info, do nothing
                if (workInfo == null) {
                    return;
                }
                // workInfo : live data where the status of work contains
                workStatus.append("Status : " + workInfo.getState().name());
                if (workInfo.getState().isFinished()) {
                    Log.d(TAG, "work: " + "succeed");
                    Toast.makeText(MainActivity.this, "result: " + workInfo.getOutputData().getString(WorkerClass.TASK_OUTPUT), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "result: " + workInfo.getOutputData().getString(WorkerClass.TASK_OUTPUT));
                }
            }
        });
    }

    @OnClick({R.id.oneTimeWork, R.id.periodicTimeWork})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.oneTimeWork:
                viewModel.startOneTimeWorkRequest();
                break;
            case R.id.periodicTimeWork:
                viewModel.startPeriodicWorkRequest();
                break;
        }
    }

}
