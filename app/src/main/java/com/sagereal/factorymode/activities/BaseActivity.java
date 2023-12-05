package com.sagereal.factorymode.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.factorymode.R;

public abstract class BaseActivity extends AppCompatActivity {
    public final int RESULT_PASS = 1111;
    public final int RESULT_FAIL = 2222;
    
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

    public abstract void initView();

    public abstract void initListener();

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public final static String SHARED_PREFERENCES_NAME = "MySharedPreferences";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //沉浸式状态栏,导航栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));//设置状态栏颜色
//            getWindow().setStatusBarColor(color);//设置状态栏颜色
//            getWindow().setNavigationBarColor(color);  //设置导航栏颜色
        }

    }
}
