package com.sagereal.factorymode.activities.test;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.sagereal.factorymode.R;

import java.io.IOException;

public class ReceiverTestActivity extends BaseTestActivity {
    MediaPlayer mediaPlayer;
    AudioManager audioManager;

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
            int id = v.getId();
            if (id == R.id.pass) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                audioManager.setMode(AudioManager.MODE_NORMAL);
                editor.putInt(STATUS_RECEIVER, 0);
                editor.commit();
                setResult(RESULT_PASS);
                finish();
            } else if (id == R.id.fail) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                audioManager.setMode(AudioManager.MODE_NORMAL);
                editor.putInt(STATUS_RECEIVER, 1);
                editor.commit();
                setResult(RESULT_FAIL);
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
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(ReceiverTestActivity.this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.anjing));
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(false);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}