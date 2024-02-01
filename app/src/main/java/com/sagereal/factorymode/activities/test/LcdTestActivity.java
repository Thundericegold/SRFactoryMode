package com.sagereal.factorymode.activities.test;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.sagereal.factorymode.R;

public class LcdTestActivity extends BaseTestActivity {
    LinearLayout lcdLayout;
    RelativeLayout startLayout,endLayout;
    Button startButton,retestButton;
    int status = 0;
    boolean isTested = false;

    @Override
    public void initView() {
        lcdLayout = findViewById(R.id.lcd_layout);
        startLayout = findViewById(R.id.start_layout);
        endLayout = findViewById(R.id.end_layout);
        startButton = findViewById(R.id.start_btn);
        retestButton = findViewById(R.id.retest_btn);
        passButton = findViewById(R.id.pass);
        failButton = findViewById(R.id.fail);
    }

    @Override
    public void initListener() {
        startButton.setOnClickListener(onClickListener);
        retestButton.setOnClickListener(onClickListener);
        passButton.setOnClickListener(onClickListener);
        failButton.setOnClickListener(onClickListener);
        lcdLayout.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = v -> {
        int id = v.getId();
        if (id == R.id.pass) {
            if (isTested) {
                editor.putInt(STATUS_LCD, VALUE_PASS);
                editor.commit();
                finish();
            }
        } else if (id == R.id.fail) {
            editor.putInt(STATUS_LCD, VALUE_FAIL);
            editor.commit();
            finish();
        }else if (id == R.id.start_btn){
            startLayout.setVisibility(View.GONE);
            lcdLayout.setVisibility(View.VISIBLE);
        } else if (id == R.id.lcd_layout) {
            if (status == 0) {
                lcdLayout.setBackgroundColor(getColor(R.color.black));
                status = 1;
            } else if (status == 1) {
                GradientDrawable gradient = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[] {0xFFFFFFFF, 0xFF000000}
                );
                lcdLayout.setBackground(gradient);
                status = 2;
                isTested = true;
            } else if (status == 2) {
                lcdLayout.setVisibility(View.GONE);
                lcdLayout.setBackgroundColor(getColor(R.color.white));
                status = 0;
                endLayout.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.retest_btn){
            isTested = false;
            endLayout.setVisibility(View.GONE);
            startLayout.setVisibility(View.VISIBLE);
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