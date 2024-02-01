package com.sagereal.factorymode.activities.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sagereal.factorymode.R;

import java.io.IOException;

public class ReceiverTestActivity extends BaseTestActivity {
    TextView tipTextView;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    private ReceiverTestActivity.HeadphoneReceiver headphoneReceiver;
    private int state = 0;
    private boolean isPlaying = false,isTested = false;

    @Override
    public void initView() {
        tipTextView = findViewById(R.id.tip_tv);
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
            int id = v.getId();
            if (id == R.id.pass) {
                if (isTested){
                    editor.putInt(STATUS_RECEIVER, VALUE_PASS);
                    editor.commit();
                    finish();
                }
            } else if (id == R.id.fail) {
                editor.putInt(STATUS_RECEIVER, VALUE_FAIL);
                editor.commit();
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_test);
        initView();
        initListener();
        headphoneReceiver = new ReceiverTestActivity.HeadphoneReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headphoneReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(ReceiverTestActivity.this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.anjing));
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(false);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            isTested = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            isTested = false;
        }
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headphoneReceiver);
    }

    private class HeadphoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                state = intent.getIntExtra("state", VALUE_DEFAULT);
                if (state == 0) {
                    if (!isPlaying){
                        tipTextView.setText(R.string.receiver_test_tip);
                        mediaPlayer = MediaPlayer.create(ReceiverTestActivity.this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.anjing));
                        mediaPlayer.start();
                        isPlaying = true;
                        isTested = true;
                    }
                } else if (state == 1) {
                    mediaPlayer.stop();
                    isPlaying = false;
                    isTested = false;
                    tipTextView.setText(R.string.receiver_test_toast_1);
                    Toast.makeText(context, getString(R.string.receiver_test_toast_1), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}