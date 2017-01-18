package com.hkid.remotecamera.ui.hiddenVideo;

import android.content.Context;

import com.data.SharedObject;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.hkid.remotecamera.ui.BaseRemoteCameraPresenterImpl;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/12/17
 */
public class HiddenVideoPresenterImpl extends BaseRemoteCameraPresenterImpl implements HiddenVideoPresenter.Presenter {
    HiddenVideoPresenter.View view;
    private boolean isRecodingVideo;
    private boolean isSwitchToFrontCamera;

    public HiddenVideoPresenterImpl(HiddenVideoPresenter.View view) {
        this.view = view;
    }

    @Override
    public void initPhoneNode(Context context) {
        view.showLoading();
//        initGoogleApiClient(context)
//                .doOnNext(googleApiClient -> Wearable.MessageApi.addListener(googleApiClient, messageListener))
//                .flatMap(googleApiClient -> findPhoneNode(googleApiClient))
//                .doOnNext(node -> phoneNode = node)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(node -> {
//                    view.hideLoading();
//                }, throwable -> {
//                    view.hideLoading();
//                    view.showMessage(throwable.getMessage());
//                });
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
    public void performSwitchCamera(boolean isSwitchToFrontCamera) {
        this.isSwitchToFrontCamera = isSwitchToFrontCamera;
    }

    @Override
    public void performRecordVideo() {
        if (phoneNode != null && googleApiClient != null) {
            SharedObject sharedObject = new SharedObject();
            sharedObject.setCommand(SharedObject.COMMAND.STOP_RECORD_VIDEO_BACKGROUND);
            String obTmp = gson.toJson(sharedObject);
            //Stop recording first
            Wearable.MessageApi.sendMessage(googleApiClient, phoneNode.getId(), obTmp, null);

            //Check to start/stop recording
            SharedObject.COMMAND command = (isRecodingVideo == true) ? SharedObject.COMMAND.STOP_RECORD_VIDEO_BACKGROUND : SharedObject.COMMAND.START_RECORD_VIDEO_BACKGROUND;
            sharedObject.setCommand(command);
            sharedObject.setSwitchToFrontCamera(isSwitchToFrontCamera);
            obTmp = gson.toJson(sharedObject);
            Wearable.MessageApi.sendMessage(googleApiClient, phoneNode.getId(), obTmp, null);
        }
    }

    @Override
    public void onMessageResult(MessageEvent messageEvents) {
        super.onMessageResult(messageEvents);
        String path = messageEvents.getPath();

        SharedObject sharedObject = gson.fromJson(path, SharedObject.class);

        if (sharedObject != null) {
            switch (sharedObject.getCommand()) {
                case START_RECORD_VIDEO_BACKGROUND:
                    isRecodingVideo = true;
                    view.switchToRecordingMode();
                    break;
                case STOP_RECORD_VIDEO_BACKGROUND:
                    isRecodingVideo = false;
                    view.switchToNormalMode();
                    view.showMessage(sharedObject.getMessage());
                    break;
            }
        }


    }

    @Override
    public Observable<Node> connectToNode(Node node) {
        return Observable.create(subscriber -> {
            if (phoneNode != null && googleApiClient != null) {
                SharedObject sharedObject = new SharedObject();
                sharedObject.setCommand(SharedObject.COMMAND.TAKE_PICTURE);
                String obTmp = gson.toJson(sharedObject);
                Wearable.MessageApi.sendMessage(googleApiClient, phoneNode.getId(), obTmp, null);
            }
        });
    }

    @Override
    public void stopRecordVideo() {
        if (phoneNode != null && googleApiClient != null) {
            SharedObject sharedObject = new SharedObject();
            sharedObject.setCommand(SharedObject.COMMAND.STOP_RECORD_VIDEO_BACKGROUND);
            String obTmp = gson.toJson(sharedObject);
            Wearable.MessageApi.sendMessage(googleApiClient, phoneNode.getId(), obTmp, null);
        }
    }

    @Override
    public void release() {

        stopRecordVideo();
        super.release();


    }
}
