package com.sagereal.factorymode.activities.test;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
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

import java.io.IOException;

public class HeadsetTestActivity extends BaseTestActivity {

    TextView tipsTextView;
    Button testButton;
    Button testingButton;
    Button retestButton;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    AudioRecorderUtil audioRecorderUtil;
    private HeadphoneReceiver headphoneReceiver;
    private final int RECORD_AUDIO_REQUEST_CODE = 10001;
    private int state = 0;

    @Override
    public void initView() {
        tipsTextView = findViewById(R.id.headset_tips);
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
            int id = v.getId();
            if (id == R.id.pass) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                }
                editor.putInt(STATUS_HEADSET, 0);
                editor.commit();
                setResult(RESULT_PASS);
                finish();
            } else if (id == R.id.fail) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                }
                editor.putInt(STATUS_HEADSET, 1);
                editor.commit();
                setResult(RESULT_FAIL);
                finish();
            } else if (id == R.id.test_btn || id == R.id.retest_btn) {
                if (state == 1) {
                    test();
                } else {
                    Toast.makeText(HeadsetTestActivity.this, getString(R.string.headset_test_toast_3), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headset_test);
        initView();
        initListener();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        headphoneReceiver = new HeadphoneReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headphoneReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headphoneReceiver);
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
            String outputPath = getExternalFilesDir(null).getAbsolutePath() + "/headsetTest.3gp";
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
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(HeadsetTestActivity.this, Uri.parse(outputPath));
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(false);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Handler handler2 = new Handler();
                Runnable task2 = () -> {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                    tipsTextView.setText(R.string.mike_test_tip_4);
                    testingButton.setVisibility(View.GONE);
                    retestButton.setVisibility(View.VISIBLE);
                };
                handler2.postDelayed(task2, 5000);
            };
            handler1.postDelayed(task1, 5000);
        }
    }

    private class HeadphoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                state = intent.getIntExtra("state", -1);
                if (state == 0) {
                    //耳机拔出
                    Toast.makeText(context, getString(R.string.headset_test_toast_2), Toast.LENGTH_SHORT).show();
                } else if (state == 1) {
                    //耳机插入
                    Toast.makeText(context, getString(R.string.headset_test_toast_1), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}