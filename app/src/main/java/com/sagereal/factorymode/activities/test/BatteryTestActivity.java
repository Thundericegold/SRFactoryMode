package com.sagereal.factorymode.activities.test;

import android.content.BroadcastReceiver;
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

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, VALUE_DEFAULT);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            if (isCharging) {
                chargingStatusTextView.setText(getString(R.string.charging));
            } else {
                chargingStatusTextView.setText(getString(R.string.uncharged));
            }

            // 获取当前电量
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, VALUE_DEFAULT);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, VALUE_DEFAULT);
            float batteryPct = level / (float) scale;
            float p = batteryPct * 100;
            currentElectricityTextView.setText(Math.round(p) + getString(R.string.electricity_unit));

            // 获取电池电压
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, VALUE_DEFAULT);
            batteryVoltageTextView.setText(voltage + getString(R.string.voltage_unit));

            // 获取电池温度
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, VALUE_DEFAULT) / 10;
            batteryTemperatureTextView.setText(temperature + getString(R.string.centigrade));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_test);
        initView();
        initListener();

        // 获取充电状态
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, ifilter);
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

    View.OnClickListener onClickListener = v -> {
        int id = v.getId();
        if (id == R.id.pass) {
            editor.putInt(STATUS_BATTERY, VALUE_PASS);
            editor.commit();
            finish();
        } else if (id == R.id.fail) {
            editor.putInt(STATUS_BATTERY, VALUE_FAIL);
            editor.commit();
            finish();
        }
    };
}