package com.sagereal.factorymode.activities.test;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sagereal.factorymode.R;

public class BatteryTestActivity extends BaseTestActivity {
    TextView chargingStatusTextView;
    TextView currentElectricityTextView;
    TextView batteryVoltageTextView;
    TextView batteryTemperatureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_test);
        initView();
        initListener();

        // 获取充电状态
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        if (isCharging){
            chargingStatusTextView.setText(getString(R.string.charging));
        }else {
            chargingStatusTextView.setText(getString(R.string.uncharged));
        }



        // 获取当前电量
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;

        currentElectricityTextView.setText(String.valueOf(batteryPct));

        // 获取电池电压
        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

        batteryVoltageTextView.setText(String.valueOf(voltage));

        // 获取电池温度
        int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

        batteryTemperatureTextView.setText(String.valueOf(temperature));
    }

    @Override
    public void initView() {
        chargingStatusTextView = findViewById(R.id.charging_status);
        currentElectricityTextView = findViewById(R.id.current_electricity);
        batteryVoltageTextView = findViewById(R.id.battery_voltage);
        batteryTemperatureTextView = findViewById(R.id.battery_temperature);
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
                    editor.putInt("battery_status",0);
                    editor.commit();
                    setResult(RESULT_PASS);
                    finish();
                    break;
                case R.id.fail:
                    editor.putInt("battery_status",1);
                    editor.commit();
                    setResult(RESULT_FAIL);
                    finish();
                    break;
            }
        }
    };
}