package com.sagereal.factorymode.activities.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sagereal.factorymode.R;

public class SpeakerTestActivity extends BaseTestActivity {

    TextView tipTextView;
    MediaPlayer mediaPlayer;
    private SpeakerTestActivity.HeadphoneReceiver headphoneReceiver;
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
                    editor.putInt(STATUS_SPEAKER, VALUE_PASS);
                    editor.commit();
                    finish();
                }
            } else if (id == R.id.fail) {
                editor.putInt(STATUS_SPEAKER, VALUE_FAIL);
                editor.commit();
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_test);
        initView();
        initListener();
        headphoneReceiver = new SpeakerTestActivity.HeadphoneReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headphoneReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer = MediaPlayer.create(SpeakerTestActivity.this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.anjing));
        mediaPlayer.start();
        isPlaying = true;
        isTested = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
        isPlaying = false;
        isTested = false;
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
                        tipTextView.setText(R.string.speaker_test_tip);
                        mediaPlayer = MediaPlayer.create(SpeakerTestActivity.this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.anjing));
                        mediaPlayer.start();
                        isPlaying = true;
                        isTested = true;
                    }
                } else if (state == 1) {
                    mediaPlayer.stop();
                    isPlaying = false;
                    isTested = false;
                    tipTextView.setText(R.string.speaker_test_toast_1);
                    Toast.makeText(context, getString(R.string.speaker_test_toast_1), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}