package com.hkid.remotecamera.ui.recordAudio;

import android.content.Context;

import com.data.SharedObject;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.hkid.remotecamera.ui.BaseRemoteCameraPresenterImpl;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/13/17
 */
public class RecordAudioPresenterImpl extends BaseRemoteCameraPresenterImpl implements RecordAudioPresenter.Presenter {

    RecordAudioPresenter.View view;
    private boolean isRecodingVideo;

    public RecordAudioPresenterImpl(RecordAudioPresenter.View view) {
        this.view = view;
    }

    @Override
    public void initPhoneNode(Context context) {
        view.showLoading();
        getPhoneNode(context)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(node -> {
                    view.hideLoading();
                }, throwable -> {
                    view.hideLoading();
                    view.showMessage(throwable.getMessage());
                });
    }

    @Override
    public void performRecordAudio() {
        if (mNode != null && mGoogleApiClient != null) {
            // Stop recording first
            stopRecordAudio();

            if(!isRecodingVideo){
                startRecordAudio();
            }
        }
    }

    @Override
    public void onMessageResult(MessageEvent messageEvents) {
        super.onMessageResult(messageEvents);
        String path = messageEvents.getPath();

        SharedObject sharedObject = gson.fromJson(path, SharedObject.class);

        if (sharedObject != null) {
            switch (sharedObject.getCommand()) {
                case START_RECORD_AUDIO:
                    isRecodingVideo = true;
                    view.switchToRecordingMode();
                    break;
                case STOP_RECORD_AUDIO:
                    isRecodingVideo = false;
                    view.switchToNormalMode();
                    view.showMessage(sharedObject.getMessage());
                    break;
            }
        }
    }

    @Override
    public void startRecordAudio() {
        if(mGoogleApiClient != null && mNode != null){
            SharedObject sharedObject = new SharedObject();
            sharedObject.setCommand(SharedObject.COMMAND.START_RECORD_AUDIO);
            String obTmp = gson.toJson(sharedObject);
            //Stop recording first
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(), obTmp, null);
        }
    }

    @Override
    public void stopRecordAudio() {
        if(mGoogleApiClient != null && mNode != null){
            SharedObject sharedObject = new SharedObject();
            sharedObject.setCommand(SharedObject.COMMAND.STOP_RECORD_AUDIO);
            String obTmp = gson.toJson(sharedObject);
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(), obTmp, null);
        }
    }

    @Override
    public void release() {
        stopRecordAudio();
        super.release();
    }
}
