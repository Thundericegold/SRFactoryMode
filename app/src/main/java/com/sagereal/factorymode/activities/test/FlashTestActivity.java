package com.sagereal.factorymode.activities.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sagereal.factorymode.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlashTestActivity extends BaseTestActivity {
    Button toggleButton;
    private CameraManager mCameraManager;
    private Boolean isTorchOn = true;

    @Override
    public void initView() {
        toggleButton = findViewById(R.id.toggle);
        passButton = findViewById(R.id.pass);
        failButton = findViewById(R.id.fail);
    }

    @Override
    public void initListener() {
        toggleButton.setOnClickListener(onClickListener);
        passButton.setOnClickListener(onClickListener);
        failButton.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.toggle) {
                try {
                    if (isTorchOn) {
                        turnOffFlashLight();
                    } else {
                        turnOnFlashLight();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (id == R.id.pass) {
                editor.putInt(STATUS_FLASH, VALUE_PASS);
                editor.commit();
                finish();
            } else if (id == R.id.fail) {
                editor.putInt(STATUS_FLASH, VALUE_FAIL);
                editor.commit();
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_test);
        initView();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isFlashAvailable) {

            AlertDialog alert = new AlertDialog.Builder(this)
                    .create();
            alert.setTitle(getString(R.string.flashlight_alert_title));
            alert.setMessage(getString(R.string.flashlight_alert_message));
            alert.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.flashlight_alert_positive), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {                    // closing the application
                    finish();
                    System.exit(0);
                }
            });
            alert.show();
            return;
        }

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        turnOnFlashLight();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isTorchOn) {
            turnOffFlashLight();
        }
    }

    public void turnOnFlashLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (String mCameraId : mCameraManager.getCameraIdList()) {
                    CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
                    Boolean flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    if (flashAvailable){
                        mCameraManager.setTorchMode(mCameraId, true);
                    }
                }
                isTorchOn = true;
                toggleButton.setText(R.string.flashlight_turn_off);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOffFlashLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (String mCameraId : mCameraManager.getCameraIdList()) {
                    CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
                    Boolean flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    if (flashAvailable){
                        mCameraManager.setTorchMode(mCameraId, false);
                    }
                }
                isTorchOn = false;
                toggleButton.setText(R.string.flashlight_turn_on);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}