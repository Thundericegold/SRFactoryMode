package com.sagereal.factorymode.activities.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.sagereal.factorymode.R;

public class KeyTestActivity extends BaseTestActivity {

    Button volumeUpButton;
    Button volumeDownButton;
    Button powerButton;
    boolean isTested = false;
    int testedNum = 0;
    String actionPowerKey = "com.sagereal.factorymode.KeyTestActivity.powerKey";
    String actionOnStart = "com.sagereal.factorymode.KeyTestActivity.onStart";
    String actionOnPause = "com.sagereal.factorymode.KeyTestActivity.onPause";

    @Override
    public void initView() {
        volumeUpButton = findViewById(R.id.volume_up_btn);
        volumeDownButton = findViewById(R.id.volume_down_btn);
        powerButton = findViewById(R.id.power_btn);
        passButton = findViewById(R.id.pass);
        failButton = findViewById(R.id.fail);
    }

    @Override
    public void initListener() {
        passButton.setOnClickListener(onClickListener);
        failButton.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = v -> {
        int id = v.getId();
        if (id == R.id.pass) {
            if (isTested) {
                editor.putInt(STATUS_KEY, VALUE_PASS);
                editor.commit();
                finish();
            }
        } else if (id == R.id.fail) {
            editor.putInt(STATUS_KEY, VALUE_FAIL);
            editor.commit();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_test);
        initView();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //电源键监听
        final IntentFilter filter = new IntentFilter(actionPowerKey);
        registerReceiver(mBatInfoReceiver, filter);
        Intent intent = new Intent(actionOnStart);
        sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBatInfoReceiver);
        Intent intent = new Intent(actionOnPause);
        sendBroadcast(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (volumeUpButton.getVisibility() == View.VISIBLE) {
                volumeUpButton.setVisibility(View.INVISIBLE);
                testedNum = testedNum + 1;
                if (testedNum == 3) {
                    isTested = true;
                }
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (volumeDownButton.getVisibility() == View.VISIBLE) {
                volumeDownButton.setVisibility(View.INVISIBLE);
                testedNum = testedNum + 1;
                if (testedNum == 3) {
                    isTested = true;
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (actionPowerKey.equals(action)) {
                if (powerButton.getVisibility() == View.VISIBLE) {
                    powerButton.setVisibility(View.INVISIBLE);
                    testedNum = testedNum + 1;
                    if (testedNum == 3) {
                        isTested = true;
                    }
                }
            }
        }
    };

}