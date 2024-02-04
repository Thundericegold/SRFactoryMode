package com.sagereal.factorymode.activities.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.sagereal.factorymode.R;

import java.io.IOException;
import java.util.List;

public class MikeTestActivity extends BaseTestActivity {

    TextView tipsTextView;
    Button testButton;
    Button testingButton;
    Button retestButton;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    private MediaRecorder mediaRecorder;
    boolean isTested = false;

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
            int id = v.getId();
            if (id == R.id.pass) {
                if (isTested) {
                    editor.putInt(STATUS_MIKE, VALUE_PASS);
                    editor.commit();
                    finish();
                }
            } else if (id == R.id.fail) {
                editor.putInt(STATUS_MIKE, VALUE_FAIL);
                editor.commit();
                finish();
            } else if (id == R.id.test_btn || id == R.id.retest_btn) {
                record();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mike_test);
        initView();
        builder = new AlertDialog.Builder(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }

    private void record() {
        isTested = false;
        String outputPath = getExternalFilesDir(null).getAbsolutePath() + "/mikeTest.3gp";
        startRecording(outputPath);
        tipsTextView.setText(R.string.mike_test_tip_2);
        testButton.setVisibility(View.GONE);
        retestButton.setVisibility(View.GONE);
        testingButton.setVisibility(View.VISIBLE);
        // 创建一个Handler对象
        Handler handler1 = new Handler();
        // 创建一个Runnable对象
        Runnable task1 = () -> {
            // 执行某个任务
            stopRecording();
            tipsTextView.setText(R.string.mike_test_tip_3);
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(MikeTestActivity.this, Uri.parse(outputPath));
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);
                fixVolume();
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Handler handler2 = new Handler();
            Runnable task2 = () -> {
                stopRecording();
                audioManager.setMode(AudioManager.MODE_NORMAL);
                tipsTextView.setText(R.string.mike_test_tip_4);
                testingButton.setVisibility(View.GONE);
                retestButton.setVisibility(View.VISIBLE);
                isTested = true;
            };
            handler2.postDelayed(task2, 5000);
        };
        handler1.postDelayed(task1, 5000);
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
                        XXPermissions.startPermissionActivity(MikeTestActivity.this, permissions);
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
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    private void fixVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume/2, AudioManager.FLAG_SHOW_UI);
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
}