package com.sagereal.factorymode.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.factorymode.R;

public abstract class BaseActivity extends AppCompatActivity {
    public final String STATUS_BATTERY = "battery_status";
    public final String STATUS_VIBRATION = "vibration_status";
    public final String STATUS_MIKE = "mike_status";
    public final String STATUS_HEADSET = "headset_status";
    public final String STATUS_LCD = "lcd_status";
    public final String STATUS_SPEAKER = "speaker_status";
    public final String STATUS_RECEIVER = "receiver_status";
    public final String STATUS_CAMERA = "camera_status";
    public final String STATUS_FLASH = "flash_status";
    public final String STATUS_KEY = "key_status";
    public final int VALUE_DEFAULT = -1;
    public final int VALUE_PASS = 0;
    public final int VALUE_FAIL = 1;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public final static String SHARED_PREFERENCES_NAME = "MySharedPreferences";

    public abstract void initView();
    public abstract void initListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setNavigationBarVisible(true);
    }

    //-----------------------防抖start---------------------------
    private long LAST_CLICK_TIME; // 上一次点击时间
    private boolean isCanClick = true;

    private void isFastClick() {
        long currentClickTime = System.currentTimeMillis();
        // 两次点击间隔不能少于500ms
        isCanClick = (currentClickTime - LAST_CLICK_TIME) >= 500;
        LAST_CLICK_TIME = currentClickTime;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!getLocalClassName().equals("activities.test.LcdTestActivity")) {
            isFastClick();
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isCanClick) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
//-----------------------防抖end---------------------------

    private void setNavigationBarVisible(boolean isHide) {
        View decorView = getWindow().getDecorView();
        int uiOptions;
        if (isHide) {
            uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
        } else {
            uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        }
        decorView.setSystemUiVisibility(uiOptions);
    }

}
