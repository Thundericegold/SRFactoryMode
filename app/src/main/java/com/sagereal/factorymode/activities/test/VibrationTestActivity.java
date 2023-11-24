package com.sagereal.factorymode.activities.test;

import android.app.Service;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.factorymode.R;

import java.security.Provider;

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

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pass:
                    stopVibrating();
                    editor.putInt(STATUS_VIBRATION,0);
                    editor.commit();
                    setResult(RESULT_PASS);
                    finish();
                    break;
                case R.id.fail:
                    stopVibrating();
                    editor.putInt(STATUS_VIBRATION,1);
                    editor.commit();
                    setResult(RESULT_FAIL);
                    finish();
                    break;
            }
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
        long[] timings = new long[] { 1000,1000,1000 };
        int[] amplitudes = new int[] { 64,0,64 };
        int repeat = 1; // Repeat from the second entry, index = 1.
        VibrationEffect repeatingEffect = VibrationEffect.createWaveform(timings, amplitudes, repeat);
        // repeatingEffect can be used in multiple places.

        vibrator.vibrate(repeatingEffect);
    }

    void stopVibrating() {
        vibrator.cancel();
    }
}