package com.sagereal.factorymode.activities.test;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.activities.BaseActivity;

public class LcdTestActivity extends BaseTestActivity {

    LinearLayout lcdLayout;
    int status = 0;

    @Override
    public void initView() {
        lcdLayout = findViewById(R.id.lcd_layout);
        passButton = findViewById(R.id.pass);
        failButton = findViewById(R.id.fail);
    }

    @Override
    public void initListener() {
        passButton.setOnClickListener(onClickListener);
        failButton.setOnClickListener(onClickListener);
        lcdLayout.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.pass:
                editor.putInt(STATUS_LCD,0);
                editor.commit();
                setResult(RESULT_PASS);
                finish();
                break;
            case R.id.fail:
                editor.putInt(STATUS_LCD,1);
                editor.commit();
                setResult(RESULT_FAIL);
                finish();
                break;
            case R.id.lcd_layout:
                if (status == 0){
                    lcdLayout.setBackgroundColor(getColor(R.color.green));
                    status = 1;
                }else if (status == 1){
                    lcdLayout.setBackgroundColor(getColor(R.color.grey));
                    status = 2;
                }else if (status == 2){
                    lcdLayout.setBackgroundColor(getColor(R.color.red));
                    status = 0;
                }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lcd_test);
        initView();
        initListener();
    }
}