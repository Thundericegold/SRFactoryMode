package com.sagereal.factorymode.activities.test;

import android.os.Bundle;
import android.view.View;
import com.sagereal.factorymode.R;

public class FlashTestActivity extends BaseTestActivity {


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
                    editor.putInt(STATUS_FLASH,0);
                    editor.commit();
                    setResult(RESULT_PASS);
                    finish();
                    break;
                case R.id.fail:
                    editor.putInt(STATUS_FLASH,1);
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
        setContentView(R.layout.activity_flash_test);
        initView();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}