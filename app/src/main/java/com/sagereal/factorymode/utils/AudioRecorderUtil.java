package com.sagereal.factorymode.utils;

import android.media.MediaRecorder;

import java.io.IOException;

public class AudioRecorderUtil {
    private MediaRecorder mediaRecorder;

    public void startRecording(String outputFile){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);

        try{
            mediaRecorder.prepare();
            mediaRecorder.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void stopRecording(){
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
    }
}
