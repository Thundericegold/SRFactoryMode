package com.sagereal.factorymode.activities.test;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.sagereal.factorymode.R;

import java.io.IOException;
import java.util.List;

public class HeadsetTestActivity extends BaseTestActivity {

    TextView tipsTextView;
    Button testButton;
    Button testingButton;
    Button retestButton;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    private MediaRecorder mediaRecorder;
    private HeadphoneReceiver headphoneReceiver;
    private int state = 0;
    boolean isTested,isRecording,isFailed = false;

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
                if (isTested) {
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                        audioManager.setMode(AudioManager.MODE_NORMAL);
                    }
                    editor.putInt(STATUS_HEADSET, 0);
                    editor.commit();
                    finish();
                }
            } else if (id == R.id.fail) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                }
                editor.putInt(STATUS_HEADSET, 1);
                editor.commit();
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
        builder = new AlertDialog.Builder(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        headphoneReceiver = new HeadphoneReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headphoneReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        XXPermissions.with(this)
                .permission(Permission.RECORD_AUDIO)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            return;
                        } else {
                            initListener();
                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        showAlertDialog(permissions);
                    }
                });
    }

    private void showAlertDialog(List<String> permissions) {
        builder.setTitle(getString(R.string.permission_alert_title))
                .setMessage(getString(R.string.permission_alert_message))
                .setPositiveButton(getString(R.string.permission_alert_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //跳转应用消息，间接打开应用权限设置-效率高
                        XXPermissions.startPermissionActivity(HeadsetTestActivity.this, permissions);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.permission_alert_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog = builder.create();
        builder.setCancelable(false);
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headphoneReceiver);
    }

    private void test() {
        isTested = false;
        isFailed = false;
        String outputPath = getExternalFilesDir(null).getAbsolutePath() + "/headsetTest.3gp";
        startRecording(outputPath);
        isRecording = true;
        tipsTextView.setText(R.string.mike_test_tip_2);
        testButton.setVisibility(View.GONE);
        retestButton.setVisibility(View.GONE);
        testingButton.setVisibility(View.VISIBLE);
        // 创建一个Handler对象
        Handler handler1 = new Handler();
        // 创建一个Runnable对象
        Runnable task1 = () -> {
            if (!isFailed) {
                // 执行某个任务
                stopRecording();
                isRecording = false;
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
                    isTested = true;
                };
                handler2.postDelayed(task2, 5000);
            }
        };
        handler1.postDelayed(task1, 5000);
    }

    private class HeadphoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                state = intent.getIntExtra("state", -1);
                if (state == 0) {
                    //耳机拔出
                    if (isRecording){
                        Toast.makeText(context, getString(R.string.headset_test_toast_4), Toast.LENGTH_SHORT).show();
                        recordeFail();
                    }else{
                        Toast.makeText(context, getString(R.string.headset_test_toast_2), Toast.LENGTH_SHORT).show();
                    }
                } else if (state == 1) {
                    //耳机插入
                    Toast.makeText(context, getString(R.string.headset_test_toast_1), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startRecording(String outputFile) {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(outputFile);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void recordeFail() {
        isFailed = true;
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
                audioManager.setMode(AudioManager.MODE_NORMAL);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        tipsTextView.setText(R.string.mike_test_tip_5);
        testingButton.setVisibility(View.GONE);
        retestButton.setVisibility(View.VISIBLE);
    }
}