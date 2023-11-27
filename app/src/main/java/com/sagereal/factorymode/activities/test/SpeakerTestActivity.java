package com.sagereal.factorymode.activities.test;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
            switch (v.getId()) {
                case R.id.pass:
                    mediaPlayer.stop();
                    editor.putInt(STATUS_SPEAKER,0);
                    editor.commit();
                    setResult(RESULT_PASS);
                    finish();
                    break;
                case R.id.fail:
                    mediaPlayer.stop();
                    editor.putInt(STATUS_SPEAKER,1);
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
        setContentView(R.layout.activity_speaker_test);
        initView();
        initListener();
        mediaPlayer = MediaPlayer.create(SpeakerTestActivity.this, Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.anjing));
        mediaPlayer.start();
    }
}