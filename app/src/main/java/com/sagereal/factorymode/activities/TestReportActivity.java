package com.sagereal.factorymode.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.factorymode.R;

public class TestReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_report);

        ImageButton backButton = findViewById(R.id.back);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.back:
                        finish();
                }
            }
        };
        backButton.setOnClickListener(onClickListener);
    }
}