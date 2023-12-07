package com.sagereal.factorymode.activities.test;

import android.app.Service;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import com.sagereal.factorymode.R;

public class VibrationTestActivity extends BaseTestActivity {

    private Vibrator vibrator;

    @Override
    public void initView() {
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
            stopVibrating();
            editor.putInt(STATUS_VIBRATION, 0);
            editor.commit();
            setResult(RESULT_PASS);
            finish();
        } else if (id == R.id.fail) {
            stopVibrating();
            editor.putInt(STATUS_VIBRATION, 1);
            editor.commit();
            setResult(RESULT_FAIL);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibration_test);
        initView();
        initListener();
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        startVibrating();
    }

    void startVibrating() {
        long[] timings = new long[]{1000, 1000, 1000};
        int[] amplitudes = new int[]{64, 0, 64};
        int repeat = 1;
        VibrationEffect repeatingEffect = VibrationEffect.createWaveform(timings, amplitudes, repeat);
        vibrator.vibrate(repeatingEffect);
    }

    void stopVibrating() {
        vibrator.cancel();
    }
}