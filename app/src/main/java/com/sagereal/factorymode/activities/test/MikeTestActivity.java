package com.sagereal.factorymode.activities.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.utils.AudioRecorderUtil;

public class MikeTestActivity extends BaseTestActivity {

    TextView tipsTextView;
    Button testButton;
    Button testingButton;
    Button retestButton;
    AudioRecorderUtil audioRecorderUtil;
    private final int RECORD_AUDIO_REQUEST_CODE = 10001;

    @Override
    public void initView() {
        tipsTextView = findViewById(R.id.mike_tips);
        testButton = findViewById(R.id.test_btn);
        testingButton = findViewById(R.id.testing_btn);
        retestButton = findViewById(R.id.retest_btn);
        passButton = findViewById(R.id.pass);
        failButton = findViewById(R.id.fail);
    }

    @Override
    public void initListener() {
        testButton.setOnClickListener(onClickListener);
        retestButton.setOnClickListener(onClickListener);
        passButton.setOnClickListener(onClickListener);
        failButton.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pass:
                    editor.putInt(STATUS_MIKE, 0);
                    editor.commit();
                    setResult(RESULT_PASS);
                    finish();
                    break;
                case R.id.fail:
                    editor.putInt(STATUS_MIKE, 1);
                    editor.commit();
                    setResult(RESULT_FAIL);
                    finish();
                    break;
                case R.id.test_btn:
                case R.id.retest_btn:
                    test();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mike_test);
        initView();
        initListener();
    }

    // 判断是否有录音权限
    private boolean ifHaveRecordAudioPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // 动态申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_REQUEST_CODE);
            return false;
        }
        return true;
    }

    // 申请权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.permission_mike), Toast.LENGTH_SHORT).show();
            } else {
               test();
            }
        }
    }

    private void test() {
        if (ifHaveRecordAudioPermission()) {
            String outputPath = getExternalFilesDir(null).getAbsolutePath() + "/mikeTest.3gp";
            audioRecorderUtil = new AudioRecorderUtil();
            audioRecorderUtil.startRecording(outputPath);
            tipsTextView.setText(R.string.mike_test_tip_2);
            testButton.setVisibility(View.GONE);
            retestButton.setVisibility(View.GONE);
            testingButton.setVisibility(View.VISIBLE);
            // 创建一个Handler对象
            Handler handler1 = new Handler();
            // 创建一个Runnable对象
            Runnable task1 = () -> {
                // 执行某个任务
                audioRecorderUtil.stopRecording();
                tipsTextView.setText(R.string.mike_test_tip_3);
                MediaPlayer mediaPlayer = MediaPlayer.create(MikeTestActivity.this, Uri.parse(outputPath));
                mediaPlayer.start();
                Handler handler2 = new Handler();
                Runnable task2 = () -> {
                    mediaPlayer.stop();
                    tipsTextView.setText(R.string.mike_test_tip_4);
                    testingButton.setVisibility(View.GONE);
                    retestButton.setVisibility(View.VISIBLE);
                };
                handler2.postDelayed(task2, 5000);
            };
            handler1.postDelayed(task1, 5000);
        }
    }
}