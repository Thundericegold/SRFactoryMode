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
                editor.putInt(STATUS_KEY, 0);
                editor.commit();
                setResult(RESULT_PASS);
                finish();
            }
        } else if (id == R.id.fail) {
            editor.putInt(STATUS_KEY, 1);
            editor.commit();
            setResult(RESULT_FAIL);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_test);
        initView();
        initListener();

        //电源键监听
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mBatInfoReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBatInfoReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (volumeUpButton.getVisibility() == View.VISIBLE) {
                volumeUpButton.setVisibility(View.GONE);
                testedNum = testedNum + 1;
                if (testedNum == 3) {
                    isTested = true;
                }
            }
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (volumeDownButton.getVisibility() == View.VISIBLE) {
                volumeDownButton.setVisibility(View.GONE);
                testedNum = testedNum + 1;
                if (testedNum == 3) {
                    isTested = true;
                }
            }
        }
//        return super.onKeyDown(keyCode, event);
        return true;
    }

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                if (powerButton.getVisibility() == View.VISIBLE) {
                    powerButton.setVisibility(View.GONE);
                    testedNum = testedNum + 1;
                    if (testedNum == 3) {
                        isTested = true;
                    }
                }
            }
        }
    };

}