package com.hkid.remotecamera.ui.hiddenPicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.data.SharedObject;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.hkid.remotecamera.R;
import com.hkid.remotecamera.ui.BaseRemoteCameraPresenterImpl;

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
        stopPreviewBackground();
        super.release();


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
        takePicture();
    }
}
