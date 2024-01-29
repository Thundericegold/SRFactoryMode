package com.sagereal.factorymode.activities.test;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.sagereal.factorymode.R;

public class SpeakerTestActivity extends BaseTestActivity {

    MediaPlayer mediaPlayer;

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
                editor.putInt(STATUS_SPEAKER, VALUE_PASS);
                editor.commit();
                finish();
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer = MediaPlayer.create(SpeakerTestActivity.this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.anjing));
        mediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }
}