package com.hkid.remotecamera.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

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

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/12/17
 */
public class BaseRemoteCameraPresenterImpl implements BaseRemoteCameraPresenter{

    protected GoogleApiClient mGoogleApiClient;
    protected Node mNote;
    protected MessageApi.MessageListener messageListener = messageEvent -> onMessageResult(messageEvent);
    protected Gson gson;

    public BaseRemoteCameraPresenterImpl() {
        gson = new Gson();
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
    public void onMessageResult(MessageEvent messageEvents) {

    }

    @Override
    public void release() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

    protected Observable<Node> getPhoneNode(Context context){
        return initGoogleApiClient(context)
                .doOnNext(googleApiClient -> Wearable.MessageApi.addListener(googleApiClient, messageListener))
                .flatMap(googleApiClient -> findPhoneNode(googleApiClient))
                .doOnNext(node -> mNote = node);
    }
}
