package com.hkid.remotecamera.ui.hiddenPicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.data.SharedObject;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/11/17
 */
public class HiddenPicturePresenterImpl implements HiddenPicturePresenter.Presenter {

    private HiddenPicturePresenter.View view;
    private Node mNote;
    private GoogleApiClient mGoogleApiClient;
    Gson gson;
    boolean switchToFrontCamera = false;

    public HiddenPicturePresenterImpl(HiddenPicturePresenter.View view) {
        this.view = view;
        gson = new Gson();
    }

    @Override
    public void performPreviewPicture(Context context) {
        view.showLoading();
        initGoogleApiClient(context)
                .doOnNext(googleApiClient -> Wearable.MessageApi.addListener(googleApiClient, messageListener))
                .flatMap(googleApiClient -> findPhoneNode(googleApiClient))
                .doOnNext(node -> mNote = node)
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
    public Observable<GoogleApiClient> initGoogleApiClient(Context context) {
        return Observable.create(new Observable.OnSubscribe<GoogleApiClient>() {
            @Override
            public void call(Subscriber<? super GoogleApiClient> subscriber) {
                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                subscriber.onNext(mGoogleApiClient);
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                subscriber.onError(new Throwable("onConnectionSuspended"));
                            }
                        })
                        .addOnConnectionFailedListener(connectionResult -> {
                            subscriber.onError(new Throwable(connectionResult.toString()));
                        })
                        .addApi(Wearable.API)
                        .build();

                mGoogleApiClient.connect();
            }
        });
    }

    @Override
    public Observable<Node> findPhoneNode(GoogleApiClient googleApiClient) {
        return Observable.create(new Observable.OnSubscribe<Node>() {
            @Override
            public void call(Subscriber<? super Node> subscriber) {
                PendingResult<NodeApi.GetConnectedNodesResult> pendingResult = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
                pendingResult.setResultCallback(getConnectedNodesResult -> {
                    if (getConnectedNodesResult.getNodes().size() > 0) {
                        Node note = getConnectedNodesResult.getNodes().get(0);
                        subscriber.onNext(note);
                    } else {
                        subscriber.onError(new Throwable("Could not find phone node"));
                    }
                });
            }
        });
    }

    @Override
    public void startPreviewBackground(boolean switchToFrontCamera) {
        if (mNote != null && mGoogleApiClient != null) {

            SharedObject sharedHiddenPictureObject = new SharedObject();
            sharedHiddenPictureObject.setCommand(SharedObject.COMMAND.START_PREVIEW_CAMERA_BACKGROUND);
            sharedHiddenPictureObject.setSwitchCamera(switchToFrontCamera);


            String obTmp = gson.toJson(sharedHiddenPictureObject);
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mNote.getId(), obTmp, null);
        }
    }

    @Override
    public void stopPreviewBackground() {
        if (mNote != null && mGoogleApiClient != null) {
            SharedObject sharedHiddenPictureObject = new SharedObject();
            sharedHiddenPictureObject.setCommand(SharedObject.COMMAND.STOP_PREVIEW_CAMERA_BACKGROUND);
            String obTmp = gson.toJson(sharedHiddenPictureObject);
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mNote.getId(), obTmp, null);
        }
    }

    @Override
    public void onMessageResult(MessageEvent messageEvents) {
        String path = messageEvents.getPath();
        if (path.equals(SharedObject.START_PREVIEW_CAMERA_BACKGROUND)) {
            byte[] data = messageEvents.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            view.bindImageView(bitmap);
            view.hideLoading();
        }
    }

    @Override
    public void release() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopPreviewBackground();
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }

    }

    MessageApi.MessageListener messageListener = messageEvent -> onMessageResult(messageEvent);

    @Override
    public void performSwitchCamera() {
        stopPreviewBackground();
        switchToFrontCamera = !switchToFrontCamera;
        startPreviewBackground(switchToFrontCamera);
    }
}
