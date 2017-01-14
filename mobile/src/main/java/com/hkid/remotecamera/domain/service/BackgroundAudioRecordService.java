package com.hkid.remotecamera.domain.service;

import android.content.Intent;
import android.media.MediaRecorder;

import com.data.SharedObject;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.hkid.remotecamera.util.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/13/17
 */
public class BackgroundAudioRecordService extends BaseRemoteCameraService {

    MediaRecorder mediaRecorder;
    private String fileName = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);

        return result;
    }

    @Override
    public void onDestroy() {
        stopRecord();
        super.onDestroy();
    }

    @Override
    protected void readyToRun() {
        super.readyToRun();
        initRecord();
        startRecord();
    }

    @Override
    protected void messageReceived(MessageEvent m) {
        String path = m.getPath();

        SharedObject sharedObject = gson.fromJson(path, SharedObject.class);
        switch (sharedObject.getCommand()) {
            case START_RECORD_AUDIO:

                break;
            case STOP_RECORD_AUDIO:

                break;
        }
    }

    private void initRecord(){
        long time = Calendar.getInstance().getTimeInMillis();
        fileName = Constants.AUDIO_FOLDER + time + Constants.AUDIO_TYPE;

        File file = new File(Constants.AUDIO_FOLDER);
        if(!file.exists()){
            file.mkdirs();
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(fileName);
    }

    private void startRecord(){
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            notifyRecordAudioStarted();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecord(){
        notifyRecordAudioStopped();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void notifyRecordAudioStarted(){
        if(mWearableNode != null && mGoogleApiClient != null){
            SharedObject sharedObjectStopRecord = new SharedObject();
            sharedObjectStopRecord.setCommand(SharedObject.COMMAND.START_RECORD_AUDIO);
            String obTmp = gson.toJson(sharedObjectStopRecord);
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mWearableNode.getId(), obTmp, null);
        }
    }

    public void notifyRecordAudioStopped(){
        if(mWearableNode != null && mGoogleApiClient != null){
            SharedObject sharedObjectStopRecord = new SharedObject();
            sharedObjectStopRecord.setCommand(SharedObject.COMMAND.STOP_RECORD_AUDIO);
            String message = "File is saved in " + fileName;
            sharedObjectStopRecord.setMessage(message);
            String obTmp = gson.toJson(sharedObjectStopRecord);
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mWearableNode.getId(), obTmp, null);
        }
    }
}
