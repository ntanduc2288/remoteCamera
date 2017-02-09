package com.hkid.remotecamera.ui.hiddenPicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;

import com.data.SharedObject;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.hkid.remotecamera.R;
import com.hkid.remotecamera.ui.BaseRemoteCameraPresenterImpl;
import com.hkid.remotecamera.ui.timer.TimerActivity;
import com.hkid.remotecamera.util.Constants;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/11/17
 */
public class HiddenPicturePresenterImpl extends BaseRemoteCameraPresenterImpl implements HiddenPicturePresenter.Presenter {

    private HiddenPicturePresenter.View view;
    boolean switchToFrontCamera = false;
    private int currentTimeRunning;
    private CountDownTimer countDownTimer;
    private boolean isCountDownRunning;

    public HiddenPicturePresenterImpl(HiddenPicturePresenter.View view) {
        this.view = view;
    }

    @Override
    public void performPreviewPicture(Context context) {
        view.showLoading();
        view.hideControlView();

        getPhoneNode(context)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(node -> {
                    stopPreviewBackground();
                    startPreviewBackground(switchToFrontCamera);
                }, throwable -> {
                    view.hideLoading();
                    view.showError(throwable.getMessage());
                });


    }

    @Override
    public void startPreviewBackground(boolean switchToFrontCamera) {
        if (phoneNode != null && googleApiClient != null) {

            SharedObject sharedHiddenPictureObject = new SharedObject();
            sharedHiddenPictureObject.setCommand(SharedObject.COMMAND.START_PREVIEW_CAMERA_BACKGROUND);
            sharedHiddenPictureObject.setSwitchToFrontCamera(switchToFrontCamera);


            String obTmp = gson.toJson(sharedHiddenPictureObject);
            Wearable.MessageApi.sendMessage(googleApiClient, phoneNode.getId(), obTmp, null);
        }
    }

    @Override
    public void stopPreviewBackground() {
        if (phoneNode != null && googleApiClient != null) {
            SharedObject sharedHiddenPictureObject = new SharedObject();
            sharedHiddenPictureObject.setCommand(SharedObject.COMMAND.STOP_PREVIEW_CAMERA_BACKGROUND);
            String obTmp = gson.toJson(sharedHiddenPictureObject);
            Wearable.MessageApi.sendMessage(googleApiClient, phoneNode.getId(), obTmp, null);
        }
    }

    @Override
    public void takePicture() {
        if (phoneNode != null && googleApiClient != null) {
            SharedObject takePictureObject = new SharedObject();
            takePictureObject.setCommand(SharedObject.COMMAND.TAKE_PICTURE);
            String obTmp = gson.toJson(takePictureObject);
            Wearable.MessageApi.sendMessage(googleApiClient, phoneNode.getId(), obTmp, null);
        }
    }

    @Override
    public void onMessageResult(MessageEvent messageEvents) {
        String path = messageEvents.getPath();
        if (path.equals(SharedObject.START_PREVIEW_CAMERA_BACKGROUND)) {
            byte[] data = messageEvents.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            view.bindPreviewImageView(bitmap);
            view.hideLoading();
            view.showControlView();
        }else if(path.equals(SharedObject.TAKE_PICTURE)){
            byte[] data = messageEvents.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            view.bindTakePictureImageView(bitmap);
        }
    }

    @Override
    public void release() {
        stopCounter();
        stopPreviewBackground();
        super.release();


    }

    @Override
    public void getTimerTime() {
        int currentTimer = TimerActivity.currentTimer;
        String timer = "";
        switch (currentTimer){
            case 0:
                timer = "Timer off";
                break;
            case 1:
                timer = "Timer 5s";
                break;
            case 2:
                timer = "Timer 10s";
                break;
            default:
                timer = "Timer off";
        }

        view.updateTimer(timer);
    }


    @Override
    public void performSwitchCamera() {
        stopPreviewBackground();
        switchToFrontCamera = !switchToFrontCamera;
        if(switchToFrontCamera){
            view.setBackgroundForSwitchCameraButton(R.drawable.rear_camera_icon);
        }else {
            view.setBackgroundForSwitchCameraButton(R.drawable.front_camera_icon);
        }
        startPreviewBackground(switchToFrontCamera);
    }

    @Override
    public void performTakePicture() {
        int currentTimer = TimerActivity.currentTimer;

        if(currentTimer == 0){
            takePicture();
        }else {
            if(isCountDownRunning){
                stopCounter();
            }else {
                startCounter(currentTimer);
            }
        }


//        takePicture();
    }

    @Override
    public void startCounter(int currentTimer){
        view.showCounterView();
        int counterTime = Constants.COUNTER_TIME[currentTimer];
        currentTimeRunning = 0;
        isCountDownRunning = true;
        countDownTimer = new CountDownTimer(counterTime * 1000 + 1000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                currentTimeRunning++;
                view.updateCounterView(String.valueOf(currentTimeRunning));
            }

            @Override
            public void onFinish() {
                currentTimeRunning++;
                view.updateCounterView("");
                view.hideCounterView();
                currentTimeRunning = 0;
                isCountDownRunning = false;
                takePicture();
            }
        }.start();

    }

    @Override
    public void stopCounter(){
        view.hideCounterView();
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        isCountDownRunning = false;
        currentTimeRunning = 0;
    }
}
