package com.sagereal.factorymode.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.activities.test.BatteryTestActivity;
import com.sagereal.factorymode.activities.test.CameraTestActivity;
import com.sagereal.factorymode.activities.test.FlashTestActivity;
import com.sagereal.factorymode.activities.test.HeadsetTestActivity;
import com.sagereal.factorymode.activities.test.KeyTestActivity;
import com.sagereal.factorymode.activities.test.LcdTestActivity;
import com.sagereal.factorymode.activities.test.MikeTestActivity;
import com.sagereal.factorymode.activities.test.ReceiverTestActivity;
import com.sagereal.factorymode.activities.test.SpeakerTestActivity;
import com.sagereal.factorymode.activities.test.VibrationTestActivity;
import com.sagereal.factorymode.adapter.TestAdapter;

import java.util.HashMap;
import java.util.Map;

public class SingleTestActivity extends BaseActivity {

    private final int REQUEST_BATTERY = 1001;
    private final int REQUEST_VIBRATION = 1002;
    private final int REQUEST_MIKE = 1003;
    private final int REQUEST_HEADSET = 1004;
    private final int REQUEST_LCD = 1005;
    private final int REQUEST_SPEAKER = 1006;
    private final int REQUEST_RECEIVER = 1007;
    private final int REQUEST_CAMERA = 1008;
    private final int REQUEST_FLASH = 1009;
    private final int REQUEST_KEY = 1010;

    ImageButton backButton;
    RecyclerView recyclerView;
    TestAdapter adapter;

    int batteryStatus, vibrationStatus, mikeStatus, headsetStatus, lcdStatus,
            speakerStatus, receiverStatus, cameraStatus, flashStatus, keyStatus;

    Map<Integer, Integer> statusList = new HashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_test);
        initView();
        initListener();
        initStatus();

        final String[] testArrays = getResources().getStringArray(R.array.test_array);
        //设置线性布局LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(SingleTestActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);
        //设置适配器
        adapter = new TestAdapter(this, testArrays, statusList, itemClickListener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initView() {
        backButton = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyview);
    }

    @Override
    public void initListener() {
        backButton.setOnClickListener(onClickListener);
    }

    private void initStatus() {
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

    TestAdapter.ClickListener itemClickListener = new TestAdapter.ClickListener() {
        @Override
        public void onClick(int position) {
            Intent intent = null;
            int requestCode = 0;
            switch (position) {
                case 0:
                    intent = new Intent(SingleTestActivity.this, BatteryTestActivity.class);
                    requestCode = REQUEST_BATTERY;
                    break;
                case 1:
                    intent = new Intent(SingleTestActivity.this, VibrationTestActivity.class);
                    requestCode = REQUEST_VIBRATION;
                    break;
                case 2:
                    intent = new Intent(SingleTestActivity.this, MikeTestActivity.class);
                    requestCode = REQUEST_MIKE;
                    break;
                case 3:
                    intent = new Intent(SingleTestActivity.this, HeadsetTestActivity.class);
                    requestCode = REQUEST_HEADSET;
                    break;
                case 4:
                    intent = new Intent(SingleTestActivity.this, LcdTestActivity.class);
                    requestCode = REQUEST_LCD;
                    break;
                case 5:
                    intent = new Intent(SingleTestActivity.this, SpeakerTestActivity.class);
                    requestCode = REQUEST_SPEAKER;
                    break;
                case 6:
                    intent = new Intent(SingleTestActivity.this, ReceiverTestActivity.class);
                    requestCode = REQUEST_RECEIVER;
                    break;
                case 7:
                    intent = new Intent(SingleTestActivity.this, CameraTestActivity.class);
                    requestCode = REQUEST_CAMERA;
                    break;
                case 8:
                    intent = new Intent(SingleTestActivity.this, FlashTestActivity.class);
                    requestCode = REQUEST_FLASH;
                    break;
                case 9:
                    intent = new Intent(SingleTestActivity.this, KeyTestActivity.class);
                    requestCode = REQUEST_KEY;
                    break;
            }
            startActivityForResult(intent, requestCode);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_BATTERY:
                updateStatus(0, sharedPreferences.getInt(STATUS_BATTERY, VALUE_DEFAULT));
                break;
            case REQUEST_VIBRATION:
                updateStatus(1, sharedPreferences.getInt(STATUS_VIBRATION, VALUE_DEFAULT));
                break;
            case REQUEST_MIKE:
                updateStatus(2, sharedPreferences.getInt(STATUS_MIKE, VALUE_DEFAULT));
                break;
            case REQUEST_HEADSET:
                updateStatus(3, sharedPreferences.getInt(STATUS_HEADSET, VALUE_DEFAULT));
                break;
            case REQUEST_LCD:
                updateStatus(4, sharedPreferences.getInt(STATUS_LCD, VALUE_DEFAULT));
                break;
            case REQUEST_SPEAKER:
                updateStatus(5, sharedPreferences.getInt(STATUS_SPEAKER, VALUE_DEFAULT));
                break;
            case REQUEST_RECEIVER:
                updateStatus(6, sharedPreferences.getInt(STATUS_RECEIVER, VALUE_DEFAULT));
                break;
            case REQUEST_CAMERA:
                updateStatus(7, sharedPreferences.getInt(STATUS_CAMERA, VALUE_DEFAULT));
                break;
            case REQUEST_FLASH:
                updateStatus(8, sharedPreferences.getInt(STATUS_FLASH, VALUE_DEFAULT));
                break;
            case REQUEST_KEY:
                updateStatus(9, sharedPreferences.getInt(STATUS_KEY, VALUE_DEFAULT));
                break;
        }
    }

    private void updateStatus(int position, int newStatus) {
        if (statusList.get(position) != newStatus) {
            statusList.put(position, newStatus);
            adapter.notifyItemChanged(position);
        }
    }
}