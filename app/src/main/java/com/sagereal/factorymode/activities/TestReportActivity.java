package com.sagereal.factorymode.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.adapter.ReportAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestReportActivity extends BaseActivity {
    RecyclerView passRecycler;
    RecyclerView failRecycler;
    RecyclerView untestedRecycler;
    ImageButton backButton;
    ReportAdapter passAdapter;
    ReportAdapter failAdapter;
    ReportAdapter untestedAdapter;

    int batteryStatus, vibrationStatus, mikeStatus, headsetStatus, lcdStatus,
            speakerStatus, receiverStatus, cameraStatus, flashStatus, keyStatus;

    Map<Integer, Integer> statusList;

    String[] testArrays;
    List<String> passList;
    List<String> failList;
    List<String> untestedList;

    @Override
    public void initView() {
        backButton = findViewById(R.id.back);
        passRecycler = findViewById(R.id.pass_recycler);
        failRecycler = findViewById(R.id.fail_recycler);
        untestedRecycler = findViewById(R.id.untested_recycler);
    }

    @Override
    public void initListener() {
        backButton.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.back:
                    finish();
            }
        }
    };

    private void initStatus() {
        passList = new ArrayList<>();
        failList = new ArrayList<>();
        untestedList = new ArrayList<>();
        testArrays = getResources().getStringArray(R.array.test_array);
        statusList = new HashMap<Integer, Integer>();

        batteryStatus = sharedPreferences.getInt(STATUS_BATTERY, VALUE_DEFAULT);
        vibrationStatus = sharedPreferences.getInt(STATUS_VIBRATION, VALUE_DEFAULT);
        mikeStatus = sharedPreferences.getInt(STATUS_MIKE, VALUE_DEFAULT);
        headsetStatus = sharedPreferences.getInt(STATUS_HEADSET, VALUE_DEFAULT);
        lcdStatus = sharedPreferences.getInt(STATUS_LCD, VALUE_DEFAULT);
        speakerStatus = sharedPreferences.getInt(STATUS_SPEAKER, VALUE_DEFAULT);
        receiverStatus = sharedPreferences.getInt(STATUS_RECEIVER, VALUE_DEFAULT);
        cameraStatus = sharedPreferences.getInt(STATUS_CAMERA, VALUE_DEFAULT);
        flashStatus = sharedPreferences.getInt(STATUS_FLASH, VALUE_DEFAULT);
        keyStatus = sharedPreferences.getInt(STATUS_KEY, VALUE_DEFAULT);

        statusList.put(0, batteryStatus);
        statusList.put(1, vibrationStatus);
        statusList.put(2, mikeStatus);
        statusList.put(3, headsetStatus);
        statusList.put(4, lcdStatus);
        statusList.put(5, speakerStatus);
        statusList.put(6, receiverStatus);
        statusList.put(7, cameraStatus);
        statusList.put(8, flashStatus);
        statusList.put(9, keyStatus);

        for (int i = 0; i <= 9; i++) {
            switch (statusList.get(i)) {
                case VALUE_DEFAULT:
                    untestedList.add(testArrays[i]);
                    break;
                case 0:
                    passList.add(testArrays[i]);
                    break;
                case 1:
                    failList.add(testArrays[i]);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_report);
        initView();
        initListener();
        initStatus();

        LinearLayoutManager passLayoutManager = new LinearLayoutManager(TestReportActivity.this, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager failLayoutManager = new LinearLayoutManager(TestReportActivity.this, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager untestedLayoutManager = new LinearLayoutManager(TestReportActivity.this, LinearLayoutManager.VERTICAL, false);

        passRecycler.setLayoutManager(passLayoutManager);
        failRecycler.setLayoutManager(failLayoutManager);
        untestedRecycler.setLayoutManager(untestedLayoutManager);

        //设置适配器
        passAdapter = new ReportAdapter(this, passList);
        failAdapter = new ReportAdapter(this, failList);
        untestedAdapter = new ReportAdapter(this, untestedList);

        passRecycler.setAdapter(passAdapter);
        failRecycler.setAdapter(failAdapter);
        untestedRecycler.setAdapter(untestedAdapter);
    }
}